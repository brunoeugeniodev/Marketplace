// /js/header.js
console.log('🔧 header.js carregado');

// Função para atualizar contador do carrinho
async function atualizarContadorCarrinho() {
    try {
        const response = await fetch('/api/carrinho');
        if (response.ok) {
            const carrinho = await response.json();
            const cartCount = document.getElementById('cart-count');
            if (cartCount && carrinho.itens) {
                const totalItens = carrinho.itens.reduce((total, item) => total + item.quantidade, 0);
                cartCount.textContent = totalItens;
                cartCount.style.display = totalItens > 0 ? 'inline-block' : 'none';
            }
        }
    } catch (error) {
        console.log('Não foi possível carregar carrinho:', error);
    }
}

// Inicializar quando o DOM estiver carregado
document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ Header inicializado');

    // Se o usuário estiver logado, atualizar carrinho
    const usuarioLogado = document.querySelector('.auth-status');
    if (usuarioLogado) {
        atualizarContadorCarrinho();
    }

    // Fechar dropdown ao clicar fora
    document.addEventListener('click', function(e) {
        const dropdowns = document.querySelectorAll('.user-dropdown');
        dropdowns.forEach(dropdown => {
            if (!dropdown.contains(e.target)) {
                const menu = dropdown.querySelector('.dropdown-menu');
                if (menu) {
                    menu.style.display = 'none';
                }
            }
        });
    });

    // Abrir/fechar dropdown do usuário
    const userBtns = document.querySelectorAll('.user-btn');
    userBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const menu = this.parentElement.querySelector('.dropdown-menu');
            if (menu) {
                menu.style.display = menu.style.display === 'block' ? 'none' : 'block';
            }
        });
    });
});