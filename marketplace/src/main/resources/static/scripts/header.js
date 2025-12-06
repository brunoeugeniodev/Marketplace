// /js/header.js
console.log('🔧 HEADER.JS SIMPLIFICADO - SEM DROPDOWN');

// Variável para controlar se já carregou
let carrinhoCarregado = false;

// Função para carregar contador do carrinho ao iniciar
async function carregarContadorCarrinho() {
    if (carrinhoCarregado) {
        console.log('⏭️ Carrinho já foi carregado, pulando...');
        return;
    }

    console.log('🛒 Carregando contador do carrinho...');

    try {
        const token = localStorage.getItem('jwtToken');
        if (!token) {
            console.log('⚠️ Usuário não logado');
            const cartCount = document.getElementById('cart-count');
            if (cartCount) {
                cartCount.textContent = '0';
                cartCount.style.display = 'none';
            }
            carrinhoCarregado = true;
            return;
        }

        const response = await fetch('/api/carrinho', {
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });

        if (response.ok) {
            const carrinho = await response.json();

            // Usar função global se disponível
            if (typeof window.atualizarContadorCarrinho === 'function') {
                window.atualizarContadorCarrinho(carrinho);
            } else {
                // Fallback
                const cartCount = document.getElementById('cart-count');
                if (cartCount && carrinho.itens) {
                    const totalItens = carrinho.itens.reduce((total, item) => total + (item.quantidade || 0), 0);
                    cartCount.textContent = totalItens;
                    cartCount.style.display = totalItens > 0 ? 'inline-block' : 'none';
                }
            }
            carrinhoCarregado = true;
            console.log('✅ Carrinho carregado com sucesso');
        } else if (response.status === 401) {
            console.log('🔓 Token inválido, limpando...');
            localStorage.removeItem('jwtToken');
            carrinhoCarregado = true;
        }
    } catch (error) {
        console.error('❌ Erro ao carregar carrinho:', error);
        carrinhoCarregado = true;
    }
}

document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ Header simplificado inicializado');

    // Carregar contador UMA ÚNICA VEZ
    carregarContadorCarrinho();

    // ========== MENU MOBILE ==========
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const navMenu = document.getElementById('nav-menu');

    if (mobileMenuBtn && navMenu) {
        mobileMenuBtn.addEventListener('click', function(e) {
            e.stopPropagation();
            navMenu.classList.toggle('show');
            const icon = this.querySelector('i');
            if (icon) {
                icon.classList.toggle('fa-bars');
                icon.classList.toggle('fa-times');
            }
        });
    }

    console.log('✅ Header pronto - SEM dropdown complexo');
});
