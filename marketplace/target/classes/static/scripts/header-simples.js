// VERSÃO SUPER SIMPLES DO HEADER.JS - APENAS PARA TESTE
console.log('🔧🔧🔧 HEADER SIMPLES CARREGADO 🔧🔧🔧');

// Esperar DOM carregar
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initHeader);
} else {
    initHeader();
}

function initHeader() {
    console.log('✅ Inicializando header...');

    const btn = document.querySelector('.user-menu-btn');
    const dropdown = document.querySelector('.user-dropdown-content');

    console.log('Botão:', btn);
    console.log('Dropdown:', dropdown);

    if (!btn || !dropdown) {
        console.error('❌ Elementos não encontrados!');
        return;
    }

    console.log('✅ Elementos encontrados!');

    // Clique no botão
    btn.onclick = function(e) {
        console.log('🖱️ CLIQUE DETECTADO!');
        e.preventDefault();
        e.stopPropagation();
        dropdown.classList.toggle('show');
        console.log('Dropdown está:', dropdown.classList.contains('show') ? 'ABERTO' : 'FECHADO');
    };

    // Fechar ao clicar fora
    document.onclick = function(e) {
        if (!btn.contains(e.target) && !dropdown.contains(e.target)) {
            dropdown.classList.remove('show');
        }
    };

    console.log('✅ Dropdown configurado com sucesso!');
}

