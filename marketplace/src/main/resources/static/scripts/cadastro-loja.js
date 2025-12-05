console.log('🔄 CADASTRO-LOJA.JS INICIANDO...');

// ========== VERSÃO MÍNIMA FUNCIONAL ==========

// Esperar DOM estar pronto
document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ DOM Carregado - Inicializando cadastro loja');

    // Elementos básicos
    const formSteps = document.querySelectorAll('.form-step');
    const stepIndicators = document.querySelectorAll('.step');
    let currentStep = 0;

    console.log(`📊 ${formSteps.length} passos encontrados`);

    if (formSteps.length === 0) {
        console.error('❌ Nenhum passo do formulário encontrado!');
        return;
    }

    // Função para mostrar um passo específico
    function showStep(stepIndex) {
        console.log(`📱 Mostrando passo ${stepIndex}`);

        // Esconder todos os passos
        formSteps.forEach(step => {
            step.classList.remove('active');
        });

        // Mostrar passo atual
        if (formSteps[stepIndex]) {
            formSteps[stepIndex].classList.add('active');
        }

        // Atualizar indicadores
        stepIndicators.forEach((indicator, index) => {
            indicator.classList.remove('active', 'completed');
            if (index < stepIndex) {
                indicator.classList.add('completed');
            } else if (index === stepIndex) {
                indicator.classList.add('active');
            }
        });

        currentStep = stepIndex;
    }

    // Função para validar um passo específico
    function validateStep(stepIndex) {
        console.log(`🔍 Validando passo ${stepIndex}`);

        const currentFormStep = formSteps[stepIndex];
        if (!currentFormStep) return false;

        const requiredInputs = currentFormStep.querySelectorAll('[required]');
        let isValid = true;

        requiredInputs.forEach(input => {
            if (!input.value.trim()) {
                const label = input.previousElementSibling?.textContent || 'Campo obrigatório';
                alert(`⚠️ Por favor, preencha: ${label.replace(':', '').trim()}`);
                input.focus();
                isValid = false;
            }
        });

        return isValid;
    }

    // Configurar botões "Próximo"
    document.querySelectorAll('.btn-next').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            console.log('👉 Botão Próximo clicado');

            if (validateStep(currentStep)) {
                const nextStep = parseInt(this.getAttribute('data-next')) - 1;
                if (nextStep < formSteps.length) {
                    showStep(nextStep);
                }
            }
        });
    });

    // Configurar botões "Anterior"
    document.querySelectorAll('.btn-prev').forEach(button => {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            const prevStep = parseInt(this.getAttribute('data-prev')) - 1;
            if (prevStep >= 0) {
                showStep(prevStep);
            }
        });
    });

    // Configurar envio do formulário
    const storeForm = document.getElementById('store-form');
    if (storeForm) {
        storeForm.addEventListener('submit', function(e) {
            e.preventDefault();
            console.log('📤 Formulário sendo enviado...');

            // Validar todos os passos
            let allValid = true;
            for (let i = 0; i < formSteps.length; i++) {
                if (!validateStep(i)) {
                    showStep(i);
                    allValid = false;
                    break;
                }
            }

            if (allValid) {
                alert('✅ Formulário válido! (Em desenvolvimento)');
                // Aqui você pode adicionar o código para enviar o formulário
                // storeForm.submit(); // Para enviar tradicionalmente
            } else {
                alert('❌ Por favor, preencha todos os campos obrigatórios.');
            }
        });
    }

    // Mostrar primeiro passo
    showStep(0);

    console.log('🎉 Cadastro loja inicializado com sucesso!');
    console.log('Teste: window._cadastroLoja = {validateStep, showStep, currentStep: ' + currentStep + '}');

    // Expor funções para debug
    window._cadastroLoja = {
        validateStep: validateStep,
        showStep: showStep,
        getCurrentStep: () => currentStep
    };
});

console.log('📄 CADASTRO-LOJA.JS CARREGADO (aguardando DOM)...');