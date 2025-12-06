// ========== HOME PAGE - Scripts específicos ==========

document.addEventListener('DOMContentLoaded', function() {
    console.log('Home page - Script carregado');

    // ========== CARROSSEL ==========
    const carrossel = document.querySelector('.carrossel');
    const produtos = document.querySelectorAll('.produto-card');
    const btnPrev = document.querySelector('.carrossel-btn.prev');
    const btnNext = document.querySelector('.carrossel-btn.next');

    if (carrossel && produtos.length > 0) {
        let currentIndex = 0;
        let produtosVisiveis = getProdutosVisiveis();

        function getProdutosVisiveis() {
            if (window.innerWidth < 768) return 1;
            if (window.innerWidth < 992) return 2;
            return 3;
        }

        function updateCarrossel() {
            if (produtos.length === 0) return;

            const produtoWidth = produtos[0].offsetWidth +
                (parseFloat(window.getComputedStyle(produtos[0]).marginLeft) || 0) +
                (parseFloat(window.getComputedStyle(produtos[0]).marginRight) || 0);

            const maxIndex = Math.max(0, produtos.length - produtosVisiveis);
            if (currentIndex > maxIndex) {
                currentIndex = maxIndex;
            }

            carrossel.style.transform = `translateX(-${currentIndex * produtoWidth}px)`;
        }

        if (btnNext && btnPrev) {
            btnNext.addEventListener('click', function() {
                const maxIndex = Math.max(0, produtos.length - produtosVisiveis);
                if (currentIndex < maxIndex) {
                    currentIndex++;
                    updateCarrossel();
                }
            });

            btnPrev.addEventListener('click', function() {
                if (currentIndex > 0) {
                    currentIndex--;
                    updateCarrossel();
                }
            });

            window.addEventListener('resize', function() {
                const novosProdutosVisiveis = getProdutosVisiveis();
                if (novosProdutosVisiveis !== produtosVisiveis) {
                    produtosVisiveis = novosProdutosVisiveis;
                    currentIndex = 0;
                    updateCarrossel();
                }
            });

            // Auto-rotacionar carrossel (só se tiver mais de 1 produto)
            if (produtos.length > 1) {
                setInterval(() => {
                    if (document.visibilityState === 'visible') {
                        const maxIndex = Math.max(0, produtos.length - produtosVisiveis);
                        currentIndex = (currentIndex + 1) % (maxIndex + 1);
                        updateCarrossel();
                    }
                }, 5000);
            }

            // Inicializar
            setTimeout(updateCarrossel, 100);
        }
    }

    // ========== ADICIONAR AO CARRINHO ==========
    const botoesAddCarrinho = document.querySelectorAll('.btn-add-carrinho');

    console.log(`🛒 Encontrados ${botoesAddCarrinho.length} botões "Adicionar ao Carrinho"`);

    botoesAddCarrinho.forEach((botao, index) => {
        console.log(`Botão ${index + 1}:`, {
            id: botao.getAttribute('data-produto-id'),
            nome: botao.getAttribute('data-produto-nome'),
            preco: botao.getAttribute('data-produto-preco')
        });

        botao.addEventListener('click', async function(e) {
            e.preventDefault();
            e.stopPropagation();

            const produtoId = this.getAttribute('data-produto-id');
            const produtoNome = this.getAttribute('data-produto-nome');

            if (!produtoId) {
                console.error('ID do produto não encontrado');
                return;
            }

            // Verificar se está logado
            const token = localStorage.getItem('jwtToken');
            if (!token) {
                if (typeof window.showNotification === 'function') {
                    window.showNotification('Você precisa estar logado para adicionar produtos ao carrinho!', 'error');
                } else {
                    alert('Você precisa estar logado para adicionar produtos ao carrinho!');
                }
                setTimeout(() => {
                    window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname);
                }, 1500);
                return;
            }

            // Desabilitar botão durante a requisição
            const originalHtml = this.innerHTML;
            this.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Adicionando...';
            this.classList.add('loading');
            this.disabled = true;

            try {
                const response = await fetch('/api/carrinho/itens', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        produtoId: parseInt(produtoId),
                        quantidade: 1
                    })
                });

                if (response.ok) {
                    const result = await response.json();

                    // Atualizar contador do carrinho usando função global
                    if (typeof window.atualizarContadorCarrinho === 'function') {
                        window.atualizarContadorCarrinho(result);
                    }

                    // Feedback visual
                    this.innerHTML = '<i class="fas fa-check"></i> Adicionado!';
                    this.style.background = '#2e7d32';

                    if (typeof window.showNotification === 'function') {
                        window.showNotification(`✅ ${produtoNome} adicionado ao carrinho!`, 'success');
                    }

                    // Restaurar botão após 2 segundos
                    setTimeout(() => {
                        this.innerHTML = originalHtml;
                        this.style.background = '';
                        this.classList.remove('loading');
                        this.disabled = false;
                    }, 2000);

                } else {
                    const error = await response.json();
                    throw new Error(error.message || 'Erro ao adicionar produto ao carrinho');
                }

            } catch (error) {
                console.error('Erro ao adicionar ao carrinho:', error);

                if (typeof window.showNotification === 'function') {
                    window.showNotification(`❌ ${error.message}`, 'error');
                } else {
                    alert(`❌ ${error.message}`);
                }

                // Restaurar botão
                this.innerHTML = originalHtml;
                this.classList.remove('loading');
                this.disabled = false;
            }
        });
    });

    // ========== BOTÃO CRIAR LOJA ==========
    const btnCriarLoja = document.getElementById('btn-criar-loja');
    if (btnCriarLoja) {
        btnCriarLoja.addEventListener('click', function() {
            const token = localStorage.getItem('jwtToken');
            if (!token) {
                showNotification('Você precisa estar logado para cadastrar uma loja!', 'error');
                setTimeout(() => {
                    window.location.href = '/login?redirect=' + encodeURIComponent('/cadastro-loja');
                }, 1500);
            } else {
                window.location.href = '/cadastro-loja';
            }
        });
    }

    // ========== EXPLORAR LOJAS ==========
    const btnExplorarLojas = document.querySelector('.btn-primary[href*="lojas"]');
    if (btnExplorarLojas) {
        btnExplorarLojas.addEventListener('click', function(e) {
            e.preventDefault();
            window.location.href = '/lojas';
        });
    }

    console.log('Home page scripts inicializados');
});