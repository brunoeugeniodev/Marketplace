#!/bin/bash
# Script de Verificação Pós-Correção
# Verifica se as correções foram aplicadas corretamente

echo "🔍 ===== VERIFICAÇÃO DE CORREÇÕES ====="
echo ""

# 1. Verificar se placeholder.png foi removido
echo "1️⃣ Verificando referências a placeholder.png..."
PLACEHOLDER_COUNT=$(grep -r "placeholder.png" target/classes/static/scripts/ 2>/dev/null | wc -l)
if [ "$PLACEHOLDER_COUNT" -eq 0 ]; then
    echo "   ✅ Nenhuma referência a placeholder.png encontrada"
else
    echo "   ❌ ERRO: Ainda há $PLACEHOLDER_COUNT referências a placeholder.png"
    grep -r "placeholder.png" target/classes/static/scripts/
fi
echo ""

# 2. Verificar se script inline foi removido do cabecalho.html
echo "2️⃣ Verificando script inline no cabecalho.html..."
INLINE_SCRIPT=$(grep -c "SCRIPT INLINE DO DROPDOWN" target/classes/templates/cabecalho.html 2>/dev/null)
if [ "$INLINE_SCRIPT" -eq 0 ]; then
    echo "   ✅ Script inline foi removido corretamente"
else
    echo "   ❌ ERRO: Script inline ainda existe no cabecalho.html"
fi
echo ""

# 3. Verificar se toggleInProgress existe no header.js
echo "3️⃣ Verificando proteção contra loops no header.js..."
TOGGLE_PROTECTION=$(grep -c "toggleInProgress" target/classes/static/scripts/header.js 2>/dev/null)
if [ "$TOGGLE_PROTECTION" -gt 0 ]; then
    echo "   ✅ Proteção contra loops implementada (toggleInProgress encontrado)"
else
    echo "   ❌ AVISO: toggleInProgress não encontrado no header.js"
fi
echo ""

# 4. Verificar se Logo.png é usado como fallback
echo "4️⃣ Verificando fallback de imagem no carrinho.js..."
LOGO_FALLBACK=$(grep -c "Logo.png" target/classes/static/scripts/carrinho.js 2>/dev/null)
if [ "$LOGO_FALLBACK" -gt 0 ]; then
    echo "   ✅ Logo.png configurado como fallback"
else
    echo "   ❌ AVISO: Logo.png não encontrado como fallback"
fi
echo ""

# 5. Contar event listeners no header.js
echo "5️⃣ Analisando event listeners no header.js..."
CLICK_LISTENERS=$(grep -c "addEventListener('click'" target/classes/static/scripts/header.js 2>/dev/null)
MOUSEDOWN_LISTENERS=$(grep -c "addEventListener('mousedown'" target/classes/static/scripts/header.js 2>/dev/null)
echo "   - Listeners 'click': $CLICK_LISTENERS"
echo "   - Listeners 'mousedown': $MOUSEDOWN_LISTENERS"
if [ "$CLICK_LISTENERS" -le 3 ] && [ "$MOUSEDOWN_LISTENERS" -eq 0 ]; then
    echo "   ✅ Quantidade de listeners está otimizada"
else
    echo "   ⚠️  AVISO: Muitos listeners podem causar problemas"
fi
echo ""

# 6. Verificar se arquivos foram sincronizados
echo "6️⃣ Verificando sincronização src -> target..."
DIFF_HEADER=$(diff -q src/main/resources/static/scripts/header.js target/classes/static/scripts/header.js 2>/dev/null)
DIFF_CARRINHO=$(diff -q src/main/resources/static/scripts/carrinho.js target/classes/static/scripts/carrinho.js 2>/dev/null)
DIFF_CABECALHO=$(diff -q src/main/resources/templates/cabecalho.html target/classes/templates/cabecalho.html 2>/dev/null)

if [ -z "$DIFF_HEADER" ] && [ -z "$DIFF_CARRINHO" ] && [ -z "$DIFF_CABECALHO" ]; then
    echo "   ✅ Arquivos sincronizados corretamente"
else
    echo "   ⚠️  AVISO: Alguns arquivos não estão sincronizados:"
    [ ! -z "$DIFF_HEADER" ] && echo "      - header.js"
    [ ! -z "$DIFF_CARRINHO" ] && echo "      - carrinho.js"
    [ ! -z "$DIFF_CABECALHO" ] && echo "      - cabecalho.html"
fi
echo ""

# Resumo final
echo "📊 ===== RESUMO ====="
ALL_GOOD=true

if [ "$PLACEHOLDER_COUNT" -ne 0 ]; then ALL_GOOD=false; fi
if [ "$INLINE_SCRIPT" -ne 0 ]; then ALL_GOOD=false; fi
if [ "$TOGGLE_PROTECTION" -eq 0 ]; then ALL_GOOD=false; fi

if [ "$ALL_GOOD" = true ]; then
    echo "✅ TODAS AS CORREÇÕES FORAM APLICADAS COM SUCESSO!"
    echo ""
    echo "🚀 Próximos passos:"
    echo "   1. Reinicie a aplicação Spring Boot"
    echo "   2. Limpe o cache do navegador (Ctrl+Shift+R)"
    echo "   3. Teste o dropdown do usuário"
    echo "   4. Verifique o console do navegador"
else
    echo "⚠️  ALGUMAS VERIFICAÇÕES FALHARAM"
    echo "   Revise os itens marcados com ❌ acima"
fi
echo ""

