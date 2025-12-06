#!/bin/bash

echo "╔══════════════════════════════════════════════════════════════════╗"
echo "║          🚀 TESTE RÁPIDO - MENU SIMPLIFICADO                     ║"
echo "╚══════════════════════════════════════════════════════════════════╝"
echo ""

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}1. Sincronizando arquivos...${NC}"
cp src/main/resources/templates/cabecalho.html target/classes/templates/cabecalho.html
cp src/main/resources/static/scripts/header.js target/classes/static/scripts/header.js
cp src/main/resources/static/estilos/style.css target/classes/static/estilos/style.css
echo -e "${GREEN}✅ Arquivos sincronizados!${NC}"
echo ""

echo -e "${BLUE}2. Verificando arquivos...${NC}"
if [ -f "target/classes/templates/cabecalho.html" ]; then
    echo -e "${GREEN}✅ cabecalho.html${NC}"
else
    echo -e "${YELLOW}⚠️  cabecalho.html não encontrado${NC}"
fi

if [ -f "target/classes/static/scripts/header.js" ]; then
    echo -e "${GREEN}✅ header.js${NC}"
else
    echo -e "${YELLOW}⚠️  header.js não encontrado${NC}"
fi

if [ -f "target/classes/static/estilos/style.css" ]; then
    echo -e "${GREEN}✅ style.css${NC}"
else
    echo -e "${YELLOW}⚠️  style.css não encontrado${NC}"
fi
echo ""

echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${GREEN}✅ PRONTO PARA TESTAR!${NC}"
echo ""
echo -e "${BLUE}📋 PRÓXIMOS PASSOS:${NC}"
echo ""
echo "   1. Reinicie o Spring Boot (Ctrl+C e reinicie)"
echo "   2. Abra: http://localhost:8080"
echo "   3. Pressione: Ctrl+Shift+R (limpar cache)"
echo "   4. Faça login"
echo ""
echo -e "${BLUE}🔍 O QUE VOCÊ VAI VER:${NC}"
echo ""
echo "   ✅ [👤 Seu Nome] [Conta] [Loja] [Sair]"
echo "   ✅ Sem dropdown, sem bugs, sem loops!"
echo ""
echo -e "${BLUE}📄 Documentação:${NC} SOLUCAO-SIMPLIFICADA.md"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

