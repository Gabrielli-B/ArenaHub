# Visão geral do ArenaHub
Gestão de campeonatos em pontos corridos: do cadastro das equipes à classificação final.

é uma aplicação web para organizar torneios esportivos no formato pontos corridos (todos contra todos, turno único). Ele substitui planilhas e anotações em papel: o organizador cadastra as equipes, cria o torneio, abre as inscrições, inicia a competição e o sistema gera a tabela de jogos automaticamente, recebe os placares e calcula a classificação em tempo real. Ao final, os dados podem ser exportados em JSON.

## O problema que resolve:

A gestão manual de campeonatos é trabalhosa e sujeita a erros: cálculo da tabela, definição dos confrontos, equilíbrio dos mandos de campo e controle das inscrições. O ArenaHub automatiza esses processos.

## Funcionalidades
Gestão de equipes: CRUD completo (nome e sigla únicos).
Gestão de torneios: CRUD com modalidade, limite de equipes (4 a 10) e controle de status (Aberto → Em Andamento → Finalizado).
Inscrições: vínculo entre torneio e equipe, com validação de limite, duplicidade e status do torneio.
Geração automática de partidas: algoritmo round-robin (método do círculo) com mandos de campo alternados.
Lançamento e correção de resultados: o torneio é finalizado automaticamente quando a última partida termina.
Classificação dinâmica: calculada na hora, com critérios de desempate.
Exportação em JSON: download do torneio completo (participantes, classificação e partidas) e da lista de equipes.
Documentação interativa da API com Swagger UI.
Tratamento padronizado de erros em português, exibido na interface.

## Telas

### Tela inicial do dashboard
![Visão geral do dashboard](images/1_page.png)
