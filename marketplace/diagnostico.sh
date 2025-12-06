#!/bin/bash

echo "🔍 DIAGNÓSTICO - Sistema Na Loja Tem"
echo "======================================"
echo ""

# Cores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 1. Verificar arquivos problemáticos
echo -e "${BLUE}[1/5]${NC} Verificando arquivos JavaScript..."
echo ""

if grep -q "beforeunload" src/main/resources/templates/minha-loja.html; then
    echo -e "${RED}❌ PROBLEMA:${NC} beforeunload encontrado em minha-loja.html (causa loop)"
else
    echo -e "${GREEN}✓${NC} minha-loja.html OK (sem beforeunload)"
fi

if grep -q "window.location.reload" src/main/resources/static/scripts/*.js 2>/dev/null; then
    echo -e "${RED}❌ PROBLEMA:${NC} location.reload encontrado nos scripts"
else
    echo -e "${GREEN}✓${NC} Scripts OK (sem location.reload)"
fi

echo ""

# 2. Verificar sincronização src -> target
echo -e "${BLUE}[2/5]${NC} Verificando sincronização de arquivos..."
echo ""

# Verificar se target existe
if [ -d "target/classes" ]; then
    echo -e "${GREEN}✓${NC} Diretório target/classes existe"

    # Contar arquivos
    SRC_COUNT=$(find src/main/resources/templates -type f | wc -l)
    TARGET_COUNT=$(find target/classes/templates -type f 2>/dev/null | wc -l)

    echo "  Templates em src: $SRC_COUNT"
    echo "  Templates em target: $TARGET_COUNT"

    if [ "$SRC_COUNT" -eq "$TARGET_COUNT" ]; then
        echo -e "${GREEN}✓${NC} Templates sincronizados"
    else
        echo -e "${YELLOW}⚠${NC} Templates desatualizados - execute ./restart.sh"
    fi
else
    echo -e "${RED}❌${NC} Diretório target não existe - compile o projeto primeiro"
fi

echo ""

# 3. Verificar imagens
echo -e "${BLUE}[3/5]${NC} Verificando imagens..."
echo ""

if [ -f "src/main/resources/static/imagens/Logo.png" ]; then
    echo -e "${GREEN}✓${NC} Logo.png existe"
else
    echo -e "${RED}❌${NC} Logo.png NÃO encontrado"
fi

if [ -f "src/main/resources/static/imagens/placeholder.png" ]; then
    echo -e "${GREEN}✓${NC} placeholder.png existe"
else
    echo -e "${YELLOW}⚠${NC} placeholder.png não existe (normal - usamos Logo.png como fallback)"
fi

echo ""

# 4. Verificar processos Java
echo -e "${BLUE}[4/5]${NC} Verificando processos Java..."
echo ""

JAVA_PROCESSES=$(ps aux | grep -i "[s]pring-boot\|[m]arketplace" | wc -l)

if [ "$JAVA_PROCESSES" -gt 0 ]; then
    echo -e "${YELLOW}⚠${NC} $JAVA_PROCESSES processo(s) Java rodando"
    echo "  Para reiniciar, pare-os primeiro: pkill -f spring-boot"
else
    echo -e "${GREEN}✓${NC} Nenhum processo Java rodando"
fi

echo ""

# 5. Verificar estrutura de arquivos críticos
echo -e "${BLUE}[5/5]${NC} Verificando arquivos críticos..."
echo ""

CRITICAL_FILES=(
    "src/main/resources/templates/cabecalho.html"
    "src/main/resources/templates/minha-loja.html"
    "src/main/resources/templates/carrinho.html"
    "src/main/resources/static/scripts/header.js"
    "src/main/resources/static/scripts/carrinho.js"
    "src/main/resources/static/estilos/style.css"
)

ALL_OK=true
for file in "${CRITICAL_FILES[@]}"; do
    if [ -f "$file" ]; then
        echo -e "${GREEN}✓${NC} $file"
    else
        echo -e "${RED}❌${NC} $file NÃO ENCONTRADO"
        ALL_OK=false
    fi
done

echo ""
echo "======================================"

if [ "$ALL_OK" = true ]; then
    echo -e "${GREEN}✅ SISTEMA OK${NC}"
    echo ""
    echo "Para iniciar o servidor:"
    echo "  1. ./restart.sh"
    echo "  2. ou: ./mvnw spring-boot:run"
    echo ""
    echo "IMPORTANTE: Limpe o cache do navegador (Ctrl+Shift+Del)"
else
    echo -e "${RED}⚠️ PROBLEMAS ENCONTRADOS${NC}"
    echo "Revise os arquivos acima marcados com ❌"
fi

echo ""

