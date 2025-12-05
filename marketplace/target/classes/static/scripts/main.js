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

    // ========== ENCONTRAR E CONFIGURAR LINKS DE LOGOUT AUTOMATICAMENTE ==========
    console.log('🔍 Procurando links de logout...');

    // Encontrar todos os possíveis links de logout
    const logoutSelectors = [
        'a[onclick*="logout"]',
        'a[href*="logout"]',
        '.logout-link',
        'a:has(i.fa-sign-out-alt)',
        '#logout-link',
        '.logout-btn'
    ];

    let logoutLinks = [];
    logoutSelectors.forEach(selector => {
        const links = document.querySelectorAll(selector);
        links.forEach(link => logoutLinks.push(link));
    });

    // Remover duplicados
    logoutLinks = [...new Set(logoutLinks)];

    console.log(`✅ ${logoutLinks.length} link(s) de logout encontrado(s)`);

    // Configurar cada link
    logoutLinks.forEach((link, index) => {
        console.log(`🔗 Configurando link ${index + 1}:`, link.tagName, link.className);

        // Remover qualquer onclick existente
        if (link.hasAttribute('onclick')) {
            link.removeAttribute('onclick');
        }

        // Remover href="#" para evitar o problema
        if (link.getAttribute('href') === '#') {
            link.setAttribute('href', 'javascript:void(0);');
        }

        // Adicionar event listener - usar captura para garantir que funcione
        link.addEventListener('click', function(e) {
            console.log('🎯 Click no link de logout detectado');
            console.log('Target:', e.target);
            console.log('Current Target:', e.currentTarget);
            window.logout(e);
        }, true); // true = usa captura phase

        // Adicionar estilo para cursor pointer se não tiver
        if (!link.style.cursor) {
            link.style.cursor = 'pointer';
        }
    });

    // ========== MENU MOBILE ==========
    const mobileMenuBtn = document.querySelector('.mobile-menu-btn');
    const navMenu = document.getElementById('nav-menu');

    if (mobileMenuBtn && navMenu) {
        mobileMenuBtn.addEventListener('click', function() {
            navMenu.classList.toggle('show');
            const icon = this.querySelector('i');
            if (icon) {
                icon.classList.toggle('fa-bars');
                icon.classList.toggle('fa-times');
            }
        });
    }

    // ========== DROPDOWN DO USUÁRIO ==========
    const userBtns = document.querySelectorAll('.user-btn');
    userBtns.forEach(btn => {
        btn.addEventListener('click', function(e) {
            e.stopPropagation();
            const dropdown = this.parentElement.querySelector('.dropdown-menu');
            if (dropdown) {
                dropdown.style.display = dropdown.style.display === 'block' ? 'none' : 'block';
            }
        });
    });

    // Fechar dropdown ao clicar fora
    document.addEventListener('click', function() {
        const dropdowns = document.querySelectorAll('.dropdown-menu');
        dropdowns.forEach(dropdown => {
            dropdown.style.display = 'none';
        });
    });

    // ========== FUNÇÃO GLOBAL PARA NOTIFICAÇÕES ==========
    window.showNotification = function(message, type = 'success') {
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
            <span>${message}</span>
            <button class="close-notification">&times;</button>
        `;

        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 5px;
            z-index: 9999;
            display: flex;
            align-items: center;
            gap: 10px;
            min-width: 300px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.2);
            animation: slideIn 0.3s ease;
            background: ${type === 'success' ? '#d4edda' : type === 'error' ? '#f8d7da' : '#d1ecf1'};
            color: ${type === 'success' ? '#155724' : type === 'error' ? '#721c24' : '#0c5460'};
            border: 1px solid ${type === 'success' ? '#c3e6cb' : type === 'error' ? '#f5c6cb' : '#bee5eb'};
        `;

        document.body.appendChild(notification);

        // Adicionar animação CSS
        if (!document.querySelector('#notification-styles')) {
            const style = document.createElement('style');
            style.id = 'notification-styles';
            style.textContent = `
                @keyframes slideIn {
                    from { transform: translateX(100%); opacity: 0; }
                    to { transform: translateX(0); opacity: 1; }
                }
            `;
            document.head.appendChild(style);
        }

        // Fechar notificação
        notification.querySelector('.close-notification').addEventListener('click', () => {
            notification.remove();
        });

        // Remover automaticamente após 5 segundos
        setTimeout(() => {
            if (notification.parentElement) {
                notification.remove();
            }
        }, 5000);
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