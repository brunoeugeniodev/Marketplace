// scripts/minha-loja.js
document.addEventListener('DOMContentLoaded', function() {
    console.log('Minha loja page - Script carregado');

    // Verificar se está logado
    const usuarioData = localStorage.getItem('usuario');
    if (!usuarioData) {
        // Não está logado, redirecionar para login
        window.location.href = '/login?redirect=' + encodeURIComponent('/minha-loja');
        return;
    }

    const usuario = JSON.parse(usuarioData);

    // Carregar dados da loja
    loadLojaData();

    async function loadLojaData() {
        try {
            const response = await fetch('/api/minha-loja/dados', {
                headers: {
                    'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
                }
            });

            if (!response.ok) {
                if (response.status === 401) {
                    // Token expirado, fazer logout
                    localStorage.removeItem('usuario');
                    localStorage.removeItem('jwtToken');
                    window.location.href = '/login?redirect=' + encodeURIComponent('/minha-loja');
                    return;
                }
                throw new Error('Erro ao carregar dados da loja');
            }

            const data = await response.json();
            renderDashboard(data);

        } catch (error) {
            console.error('Erro ao carregar dados:', error);

            // Tentar carregar da página HTML (dados renderizados pelo Thymeleaf)
            const lojaElement = document.querySelector('.loja-info');
            if (!lojaElement) {
                showNoStoreMessage();
            }
        }
    }

    function renderDashboard(data) {
        console.log('Dados da loja:', data);

        // Atualizar informações básicas
        if (data.nome) {
            document.title = `${data.nome} | Minha Loja`;
        }

        // Se tiver dados de produtos, renderizar tabela
        if (data.produtos && data.produtos.length > 0) {
            renderProdutosTable(data.produtos);
        }
    }

    function renderProdutosTable(produtos) {
        const tbody = document.querySelector('.loja-produtos tbody');
        if (!tbody) return;

        tbody.innerHTML = '';

        produtos.forEach(produto => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${produto.nome}</td>
                <td>R$ ${produto.preco.toFixed(2)}</td>
                <td>
                    <a href="/produto/editar/${produto.id}" class="btn btn-sm">
                        <i class="fas fa-edit"></i> Editar
                    </a>
                </td>
            `;
            tbody.appendChild(row);
        });
    }

    function showNoStoreMessage() {
        // A mensagem já deve estar no HTML via Thymeleaf
        console.log('Usuário não tem loja cadastrada');
    }

    // Event listeners para botões
    const cadastrarLojaBtn = document.querySelector('a[href="/cadastro-loja"]');
    if (cadastrarLojaBtn) {
        cadastrarLojaBtn.addEventListener('click', function(e) {
            e.preventDefault();
            window.location.href = '/cadastro-loja';
        });
    }

    const editarLojaBtn = document.querySelector('a[href*="editar=true"]');
    if (editarLojaBtn) {
        editarLojaBtn.addEventListener('click', function(e) {
            e.preventDefault();
            const url = new URL(this.href);
            window.location.href = url;
        });
    }
});