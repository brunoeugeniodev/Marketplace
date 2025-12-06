// ====================================================
// SCRIPT DE DIAGNÓSTICO DO DROPDOWN
// ====================================================
// Cole este script no Console do navegador (F12 → Console)
// e clique no botão do dropdown para ver diagnóstico completo
// ====================================================

console.log('🔧 Iniciando diagnóstico do dropdown...');

// 1. Verificar se os elementos existem
const btn = document.querySelector('.user-menu-btn');
const dropdown = document.querySelector('.user-dropdown-content');

console.log('📋 VERIFICAÇÃO DE ELEMENTOS:');
console.log('  ✓ Botão encontrado:', !!btn, btn);
console.log('  ✓ Dropdown encontrado:', !!dropdown, dropdown);

if (!btn || !dropdown) {
    console.error('❌ ERRO: Elementos não encontrados!');
} else {
    // 2. Verificar estilos computados
    const btnStyles = window.getComputedStyle(btn);
    const dropdownStyles = window.getComputedStyle(dropdown);

    console.log('📋 ESTILOS DO BOTÃO:');
    console.log('  - display:', btnStyles.display);
    console.log('  - visibility:', btnStyles.visibility);
    console.log('  - opacity:', btnStyles.opacity);
    console.log('  - pointer-events:', btnStyles.pointerEvents);
    console.log('  - z-index:', btnStyles.zIndex);
    console.log('  - position:', btnStyles.position);
    console.log('  - cursor:', btnStyles.cursor);

    console.log('📋 ESTILOS DO DROPDOWN:');
    console.log('  - display:', dropdownStyles.display);
    console.log('  - visibility:', dropdownStyles.visibility);
    console.log('  - opacity:', dropdownStyles.opacity);
    console.log('  - z-index:', dropdownStyles.zIndex);

    // 3. Verificar posição e dimensões
    const btnRect = btn.getBoundingClientRect();
    console.log('📋 POSIÇÃO E DIMENSÕES DO BOTÃO:');
    console.log('  - top:', btnRect.top);
    console.log('  - left:', btnRect.left);
    console.log('  - width:', btnRect.width);
    console.log('  - height:', btnRect.height);

    // 4. Verificar o que está na posição do botão
    const centerX = btnRect.left + btnRect.width / 2;
    const centerY = btnRect.top + btnRect.height / 2;
    const elementAtCenter = document.elementFromPoint(centerX, centerY);

    console.log('📋 VERIFICAÇÃO DE SOBREPOSIÇÃO:');
    console.log('  - Elemento no centro do botão:', elementAtCenter);
    console.log('  - É o próprio botão?', elementAtCenter === btn);
    console.log('  - Está dentro do botão?', btn.contains(elementAtCenter));

    if (elementAtCenter !== btn && !btn.contains(elementAtCenter)) {
        console.warn('⚠️ PROBLEMA DETECTADO: Há um elemento sobrepondo o botão!');
        console.log('  - Elemento que está na frente:', elementAtCenter);
        const overlapStyles = window.getComputedStyle(elementAtCenter);
        console.log('  - z-index do elemento:', overlapStyles.zIndex);
        console.log('  - position:', overlapStyles.position);
        console.log('  - pointer-events:', overlapStyles.pointerEvents);
    }

    // 5. Verificar event listeners
    console.log('📋 EVENT LISTENERS:');
    const listeners = getEventListeners(btn);
    if (listeners) {
        Object.keys(listeners).forEach(eventType => {
            console.log(`  - ${eventType}: ${listeners[eventType].length} listener(s)`);
        });
    } else {
        console.log('  ⚠️ getEventListeners não disponível (use Chrome/Edge)');
    }

    // 6. Teste interativo
    console.log('📋 TESTE INTERATIVO:');
    console.log('  Clique no botão agora e veja os logs abaixo...');

    let clickCount = 0;
    let mousedownCount = 0;
    let mouseupCount = 0;

    const testMousedown = (e) => {
        mousedownCount++;
        console.log(`🖱️ [${mousedownCount}] MOUSEDOWN detectado:`, {
            button: e.button,
            buttons: e.buttons,
            target: e.target.className,
            currentTarget: e.currentTarget.className
        });
    };

    const testMouseup = (e) => {
        mouseupCount++;
        console.log(`🖱️ [${mouseupCount}] MOUSEUP detectado:`, {
            button: e.button,
            buttons: e.buttons
        });
    };

    const testClick = (e) => {
        clickCount++;
        console.log(`🖱️ [${clickCount}] CLICK detectado:`, {
            button: e.button,
            buttons: e.buttons,
            target: e.target.className,
            currentTarget: e.currentTarget.className
        });
    };

    btn.addEventListener('mousedown', testMousedown, true);
    btn.addEventListener('mouseup', testMouseup, true);
    btn.addEventListener('click', testClick, true);

    console.log('✅ Diagnóstico configurado! Clique no botão do dropdown agora.');
    console.log('');
    console.log('💡 DICA: Se você NÃO ver mensagens de MOUSEDOWN/CLICK quando clicar,');
    console.log('   significa que algo está bloqueando os eventos antes de chegarem ao botão.');
}

// 7. Listar todos os elementos com position fixed/absolute
console.log('📋 ELEMENTOS COM POSITION FIXED/ABSOLUTE:');
const allElements = document.querySelectorAll('*');
let fixedElements = [];
let absoluteElements = [];

allElements.forEach(el => {
    const style = window.getComputedStyle(el);
    if (style.position === 'fixed') {
        fixedElements.push({
            element: el,
            zIndex: style.zIndex,
            className: el.className
        });
    } else if (style.position === 'absolute') {
        absoluteElements.push({
            element: el,
            zIndex: style.zIndex,
            className: el.className
        });
    }
});

console.log(`  - ${fixedElements.length} elementos com position: fixed`);
console.log(`  - ${absoluteElements.length} elementos com position: absolute`);

// Mostrar apenas os com z-index alto
const highZIndex = [...fixedElements, ...absoluteElements]
    .filter(item => parseInt(item.zIndex) > 0)
    .sort((a, b) => parseInt(b.zIndex) - parseInt(a.zIndex));

if (highZIndex.length > 0) {
    console.log('  Elementos com z-index alto (que podem estar na frente):');
    highZIndex.slice(0, 10).forEach(item => {
        console.log(`    - z-index ${item.zIndex}: ${item.className || item.element.tagName}`);
    });
}

console.log('');
console.log('🎯 DIAGNÓSTICO COMPLETO!');
console.log('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');

