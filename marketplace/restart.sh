#!/bin/bash

echo "🔄 Script de Reinicialização - Na Loja Tem"
echo "=========================================="
echo ""

# Cores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 1. Para qualquer processo Java/Spring em execução
echo -e "${BLUE}[1/4]${NC} Parando processos Java..."
pkill -f "spring-boot" 2>/dev/null || true
pkill -f "marketplace" 2>/dev/null || true
sleep 2
echo -e "${GREEN}✓${NC} Processos parados"
echo ""

# 2. Copia arquivos atualizados
echo -e "${BLUE}[2/4]${NC} Copiando arquivos atualizados..."
cp -r src/main/resources/static/* target/classes/static/ 2>/dev/null || true
cp -r src/main/resources/templates/* target/classes/templates/ 2>/dev/null || true
echo -e "${GREEN}✓${NC} Arquivos copiados"
echo ""

# 3. Limpa cache do navegador (instrução)
echo -e "${BLUE}[3/4]${NC} Limpando configurações..."
echo "   IMPORTANTE: Limpe o cache do navegador:"
echo "   - Chrome/Edge: Ctrl+Shift+Del"
echo "   - Firefox: Ctrl+Shift+Del"
echo ""

# 4. Verifica se o JAR existe
echo -e "${BLUE}[4/4]${NC} Verificando aplicação..."
if [ -f "target/marketplace-0.0.1-SNAPSHOT.jar" ]; then
    echo -e "${GREEN}✓${NC} JAR encontrado"
    echo ""
    echo "=========================================="
    echo -e "${GREEN}Tudo pronto!${NC}"
    echo ""
    echo "Para iniciar a aplicação, execute:"
    echo -e "${BLUE}java -jar target/marketplace-0.0.1-SNAPSHOT.jar${NC}"
    echo ""
elif [ -x "./mvnw" ]; then
    echo -e "${GREEN}✓${NC} Maven wrapper encontrado"
    echo ""
    echo "=========================================="
    echo -e "${GREEN}Tudo pronto!${NC}"
    echo ""
    echo "Para iniciar a aplicação, execute:"
    echo -e "${BLUE}./mvnw spring-boot:run${NC}"
    echo ""
else
    echo -e "${RED}⚠${NC} Nem JAR nem Maven encontrados"
    echo ""
    echo "Por favor, compile o projeto primeiro:"
    echo "  mvn clean package -DskipTests"
    echo ""
fi

echo "Acesse: http://localhost:8080"
echo "=========================================="

