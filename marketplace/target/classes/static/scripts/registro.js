// scripts/registro.js
document.addEventListener('DOMContentLoaded', function() {
    console.log('Registro page - Script carregado');

    // ========== MÁSCARAS DE FORMATAÇÃO ==========

    // Máscara para CPF
    const cpfInput = document.getElementById('cpf');
    if (cpfInput) {
        cpfInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');

            if (value.length > 11) {
                value = value.substring(0, 11);
            }

            // Formatar CPF: 000.000.000-00
            if (value.length > 9) {
                value = value.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})/, '$1.$2.$3-$4');
            } else if (value.length > 6) {
                value = value.replace(/^(\d{3})(\d{3})(\d{0,3})/, '$1.$2.$3');
            } else if (value.length > 3) {
                value = value.replace(/^(\d{3})(\d{0,3})/, '$1.$2');
            }

            e.target.value = value;
        });

        // Validação de CPF
        cpfInput.addEventListener('blur', function() {
            const cpf = this.value.replace(/\D/g, '');
            if (cpf.length === 11 && !validarCPF(cpf)) {
                showFieldError(this, 'CPF inválido');
            } else {
                removeFieldError(this);
            }
        });
    }

    // Máscara para Telefone
    const telefoneInput = document.getElementById('telefone');
    if (telefoneInput) {
        telefoneInput.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');

            // Formatar telefone: (00) 00000-0000 ou (00) 0000-0000
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
    }

    // ========== VALIDAÇÃO DE SENHA ==========
    const senhaInput = document.getElementById('senha');
    const confirmarSenhaInput = document.getElementById('confirmar-senha');

    if (senhaInput && confirmarSenhaInput) {
        // Validar força da senha
        senhaInput.addEventListener('input', function() {
            const senha = this.value;
            const feedback = document.getElementById('senha-feedback');

            if (!feedback) {
                createPasswordFeedback();
            }

            updatePasswordStrength(senha);
        });

        // Confirmar senha
        confirmarSenhaInput.addEventListener('input', function() {
            const senha = senhaInput.value;
            const confirmar = this.value;

            if (confirmar && senha !== confirmar) {
                showFieldError(this, 'As senhas não coincidem');
            } else {
                removeFieldError(this);
            }
        });
    }

    // ========== VALIDAÇÃO DE EMAIL ==========
    const emailInput = document.getElementById('email');
    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            const email = this.value;
            if (email && !validarEmail(email)) {
                showFieldError(this, 'Email inválido');
            } else {
                removeFieldError(this);
            }
        });
    }

    // ========== VALIDAÇÃO DO FORMULÁRIO ==========
    const registroForm = document.querySelector('form[action*="registro"]');
    if (registroForm) {
        registroForm.addEventListener('submit', function(e) {
            console.log('Validando formulário de registro...');

            let isValid = true;
            const requiredFields = this.querySelectorAll('[required]');

            // Validar campos obrigatórios
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    const fieldName = field.previousElementSibling?.textContent || 'Campo obrigatório';
                    showFieldError(field, `${fieldName.replace(':', '').trim()} é obrigatório`);
                    isValid = false;
                }
            });

            // Validar CPF
            if (cpfInput && cpfInput.value.replace(/\D/g, '').length !== 11) {
                showFieldError(cpfInput, 'CPF deve ter 11 dígitos');
                isValid = false;
            }

            // Validar telefone
            if (telefoneInput && telefoneInput.value.replace(/\D/g, '').length < 10) {
                showFieldError(telefoneInput, 'Telefone deve ter pelo menos 10 dígitos');
                isValid = false;
            }

            // Validar senhas
            if (senhaInput && confirmarSenhaInput) {
                if (senhaInput.value !== confirmarSenhaInput.value) {
                    showFieldError(confirmarSenhaInput, 'As senhas não coincidem');
                    isValid = false;
                }

                if (senhaInput.value.length < 6) {
                    showFieldError(senhaInput, 'A senha deve ter pelo menos 6 caracteres');
                    isValid = false;
                }
            }

            if (!isValid) {
                e.preventDefault();
                showNotification('Por favor, corrija os erros no formulário', 'error');
            } else {
                console.log('Formulário válido, enviando...');
                // Mostrar loading
                const submitBtn = this.querySelector('button[type="submit"]');
                if (submitBtn) {
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Cadastrando...';
                    submitBtn.disabled = true;
                }
            }
        });
    }

    // ========== FUNÇÕES AUXILIARES ==========

    function createPasswordFeedback() {
        const feedback = document.createElement('div');
        feedback.id = 'senha-feedback';
        feedback.className = 'password-feedback';
        feedback.innerHTML = `
            <div class="strength-meter">
                <div class="strength-bar"></div>
            </div>
            <small class="strength-text">Força da senha: <span>Fraca</span></small>
            <small class="password-tips">
                <i class="fas fa-info-circle"></i>
                Use pelo menos 6 caracteres, incluindo letras e números
            </small>
        `;

        const senhaGroup = senhaInput.closest('.form-group');
        if (senhaGroup) {
            senhaGroup.appendChild(feedback);
        }
    }

    function updatePasswordStrength(password) {
        const feedback = document.getElementById('senha-feedback');
        if (!feedback) return;

        const strengthBar = feedback.querySelector('.strength-bar');
        const strengthText = feedback.querySelector('.strength-text span');

        let strength = 0;
        let color = '#e74c3c';
        let text = 'Fraca';

        // Critérios de força
        if (password.length >= 6) strength += 1;
        if (password.length >= 8) strength += 1;
        if (/[A-Z]/.test(password)) strength += 1;
        if (/[0-9]/.test(password)) strength += 1;
        if (/[^A-Za-z0-9]/.test(password)) strength += 1;

        // Determinar força
        if (strength >= 4) {
            color = '#2ecc71';
            text = 'Forte';
        } else if (strength >= 2) {
            color = '#f39c12';
            text = 'Média';
        }

        // Atualizar visual
        strengthBar.style.width = `${(strength / 5) * 100}%`;
        strengthBar.style.backgroundColor = color;
        strengthText.textContent = text;
        strengthText.style.color = color;
    }

    function showFieldError(field, message) {
        removeFieldError(field);

        const errorDiv = document.createElement('div');
        errorDiv.className = 'field-error';
        errorDiv.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;

        field.style.borderColor = '#e74c3c';
        field.parentElement.appendChild(errorDiv);
    }

    function removeFieldError(field) {
        const existingError = field.parentElement.querySelector('.field-error');
        if (existingError) {
            existingError.remove();
        }
        field.style.borderColor = '';
    }

    function validarEmail(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    }

    function validarCPF(cpf) {
        // Remover caracteres não numéricos
        cpf = cpf.replace(/[^\d]/g, '');

        // Verificar se tem 11 dígitos
        if (cpf.length !== 11) return false;

        // Verificar se é uma sequência de números repetidos
        if (/^(\d)\1+$/.test(cpf)) return false;

        // Validar primeiro dígito verificador
        let soma = 0;
        for (let i = 0; i < 9; i++) {
            soma += parseInt(cpf.charAt(i)) * (10 - i);
        }
        let resto = (soma * 10) % 11;
        if (resto === 10 || resto === 11) resto = 0;
        if (resto !== parseInt(cpf.charAt(9))) return false;

        // Validar segundo dígito verificador
        soma = 0;
        for (let i = 0; i < 10; i++) {
            soma += parseInt(cpf.charAt(i)) * (11 - i);
        }
        resto = (soma * 10) % 11;
        if (resto === 10 || resto === 11) resto = 0;
        if (resto !== parseInt(cpf.charAt(10))) return false;

        return true;
    }

    function showNotification(message, type = 'info') {
        // Reutilizar a função do main.js se existir
        if (typeof window.showNotification === 'function') {
            window.showNotification(message, type);
            return;
        }

        // Fallback local
        const notification = document.createElement('div');
        notification.className = `notification notification-${type}`;
        notification.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'error' ? 'exclamation-circle' : 'info-circle'}"></i>
            <span>${message}</span>
        `;

        notification.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            padding: 15px 20px;
            border-radius: 5px;
            background: ${type === 'success' ? '#d4edda' : type === 'error' ? '#f8d7da' : '#d1ecf1'};
            color: ${type === 'success' ? '#155724' : type === 'error' ? '#721c24' : '#0c5460'};
            border: 1px solid ${type === 'success' ? '#c3e6cb' : type === 'error' ? '#f5c6cb' : '#bee5eb'};
            z-index: 9999;
            display: flex;
            align-items: center;
            gap: 10px;
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 5000);
    }

    console.log('Registro scripts inicializados');
});