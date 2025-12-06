// scripts/main.js simplificado
document.addEventListener('DOMContentLoaded', function() {
    console.log('Script principal carregado');

    // ========== FUNÇÃO GLOBAL PARA LOGOUT ==========
    window.logout = function(event) {
        console.log('🚀 Função logout chamada');

        // Se um evento foi passado, prevenir comportamento padrão
        if (event) {
            event.preventDefault();
            event.stopPropagation();
        }

        // Criar um formulário para logout (mais confiável que fetch)
        console.log('📝 Criando formulário de logout...');
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '/logout';
        form.style.display = 'none';

        // Adicionar CSRF token se existir
        const csrfToken = document.querySelector('meta[name="_csrf"]');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]');

        if (csrfToken && csrfHeader) {
            const input = document.createElement('input');
            input.type = 'hidden';
            // CORREÇÃO AQUI: Usar o nome correto do CSRF
            input.name = csrfHeader.content === 'X-CSRF-TOKEN' ? '_csrf' : '_csrf';
            input.value = csrfToken.content;
            form.appendChild(input);
            console.log('🔐 CSRF token adicionado:', csrfToken.content.substring(0, 10) + '...');
        } else {
            console.warn('⚠️ CSRF token não encontrado! Verifique se está sendo gerado no template.');
        }

        document.body.appendChild(form);

        console.log('🔄 Submetendo formulário de logout...');
        form.submit();

        return false;
    };

    // NOTA: Links de logout agora são gerenciados por header.js
    // Removido código que causava conflito com o dropdown do usuário

    // NOTA: Menu mobile agora é gerenciado por header.js
    // NOTA: O dropdown do usuário agora é gerenciado por header.js

    // ========== FUNÇÃO GLOBAL PARA NOTIFICAÇÕES ==========
    window.showNotification = function(message, type = 'success') {
        // Remover notificação anterior se existir
        const oldNotification = document.querySelector('.notification');
        if (oldNotification) {
            oldNotification.remove();
        }

        // Criar elemento de notificação
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <span class="notification-message">${message}</span>
            <button class="notification-close" onclick="this.parentElement.remove()">
                <i class="fas fa-times"></i>
            </button>
        `;

        // Adicionar estilos inline se não existirem no CSS
        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 5px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            z-index: 10000;
            display: flex;
            align-items: center;
            gap: 10px;
            animation: slideIn 0.3s ease-out;
            max-width: 400px;
            background: ${type === 'success' ? '#4CAF50' : type === 'error' ? '#f44336' : '#2196F3'};
            color: white;
        `;

        document.body.appendChild(notification);

        // Remover após 3 segundos
        setTimeout(() => {
            notification.style.animation = 'slideOut 0.3s ease-out';
            setTimeout(() => notification.remove(), 300);
        }, 3000);
    };

    // ========== FUNÇÃO GLOBAL PARA ATUALIZAR CONTADOR DO CARRINHO ==========
    window.atualizarContadorCarrinho = function(carrinhoData) {
        const cartCount = document.getElementById('cart-count');
        if (!cartCount) {
            console.warn('⚠️ Elemento cart-count não encontrado');
            return;
        }

        let totalItens = 0;

        if (carrinhoData && carrinhoData.itens && Array.isArray(carrinhoData.itens)) {
            // Somar as quantidades de todos os itens
            totalItens = carrinhoData.itens.reduce((total, item) => total + (item.quantidade || 0), 0);
        }

        console.log(`🛒 Atualizando contador do carrinho: ${totalItens} itens`);

        cartCount.textContent = totalItens;

        // Mostrar/ocultar o badge
        if (totalItens > 0) {
            cartCount.style.display = 'inline-block';
            cartCount.classList.add('show');
        } else {
            cartCount.style.display = 'none';
            cartCount.classList.remove('show');
        }
    };


    // ========== FUNÇÕES ADICIONAIS PARA O SITE ==========

    // Função para formatação de moeda
    window.formatarMoeda = function(valor) {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(valor);
    };

    // Função para formatação de data
    window.formatarData = function(dataString) {
        const data = new Date(dataString);
        return data.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    // Função para validação de email
    window.validarEmail = function(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    };

    // Função para máscara de telefone
    window.aplicarMascaraTelefone = function(input) {
        input.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length > 10) {
                value = value.replace(/^(\d{2})(\d{5})(\d{4}).*/, '($1) $2-$3');
            } else if (value.length > 6) {
                value = value.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, '($1) $2-$3');
            } else if (value.length > 2) {
                value = value.replace(/^(\d{2})(\d{0,5})/, '($1) $2');
            } else if (value.length > 0) {
                value = value.replace(/^(\d*)/, '($1');
            }
            e.target.value = value;
        });
    };

    // Aplicar máscara em todos os inputs de telefone
    document.querySelectorAll('input[type="tel"]').forEach(input => {
        window.aplicarMascaraTelefone(input);
    });

    // Função para mostrar/ocultar senha
    window.toggleSenha = function(inputId) {
        const input = document.getElementById(inputId);
        if (input) {
            input.type = input.type === 'password' ? 'text' : 'password';
        }
    };

    // Adicionar ícones de olho para inputs de senha
    document.querySelectorAll('input[type="password"]').forEach(input => {
        const wrapper = document.createElement('div');
        wrapper.style.position = 'relative';
        wrapper.style.display = 'inline-block';
        wrapper.style.width = '100%';

        input.parentNode.insertBefore(wrapper, input);
        wrapper.appendChild(input);

        const toggleBtn = document.createElement('button');
        toggleBtn.type = 'button';
        toggleBtn.innerHTML = '<i class="fas fa-eye"></i>';
        toggleBtn.style.position = 'absolute';
        toggleBtn.style.right = '10px';
        toggleBtn.style.top = '50%';
        toggleBtn.style.transform = 'translateY(-50%)';
        toggleBtn.style.background = 'none';
        toggleBtn.style.border = 'none';
        toggleBtn.style.cursor = 'pointer';
        toggleBtn.style.color = '#666';

        toggleBtn.addEventListener('click', function() {
            const icon = this.querySelector('i');
            if (input.type === 'password') {
                input.type = 'text';
                icon.classList.remove('fa-eye');
                icon.classList.add('fa-eye-slash');
            } else {
                input.type = 'password';
                icon.classList.remove('fa-eye-slash');
                icon.classList.add('fa-eye');
            }
        });

        wrapper.appendChild(toggleBtn);
    });

    console.log('✅ Script principal inicializado com sucesso');
});

// Garantir que a função está disponível globalmente mesmo antes do DOM
console.log('🌐 main.js carregado - logout disponível globalmente:', typeof window.logout);

// Debug: Verificar se os elementos estão sendo carregados
setTimeout(() => {
    console.log('🔍 Verificando elementos de logout após carregamento...');
    const logoutLinks = document.querySelectorAll('.logout-link, a[href*="logout"], .logout-btn');
    console.log('Elementos de logout encontrados:', logoutLinks.length);
    logoutLinks.forEach((link, i) => {
        console.log(`Link ${i + 1}:`, {
            tag: link.tagName,
            id: link.id,
            class: link.className,
            href: link.getAttribute('href'),
            onclick: link.getAttribute('onclick')
        });
    });
}, 1000);