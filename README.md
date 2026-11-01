# Banana Create (addon do Create, NeoForge 1.21.1)

## Gerar o .jar

Requisitos: Java 21 (JDK) e internet (o Gradle baixa NeoForge, Create, Ponder e Flywheel).

```
./gradlew build        # Windows: gradlew.bat build
```

O mod fica em `build/libs/bananacreate-0.7.1.jar`.

Para jogar: coloque esse `.jar` na pasta `mods` de uma instalacao NeoForge 1.21.1,
junto com **Create 6.0.6+**, **Ponder** e **Flywheel** (o Create ja pede os dois).

Para testar direto do codigo, sem gerar o jar: `./gradlew runClient`.

## O que tem

- Bananeira de verdade: tronco, tabuas, folhas que dao banana (clique direito), muda e geracao em selvas.
- Canhao de Banana e ferramentas de banana.
- Fluido de Sangue + Balde de Sangue.
- Extrator de Sangue: maquina cinetica do Create. Gira -> fere mobs em cima e enche tanque.
- Centrifuga de Sangue: maquina cinetica. Recebe sangue (canos do Create) -> Frascos de Sangue.
- Seringa de Tita: clique direito em mob hostil -> vira tita (escala x6).
- Esteira de Tita: GERADOR cinetico. Titas por perto andam em circulo e geram 16 RPM cada (max 64).
  Monte uma arena cercada de uns 13x13 com a esteira no centro.
- Cyberware: Placa Cromada (prensa do Create), Chip Neural, Laminas de Braco Cromadas, Injetor de Overclock.
- Martelo do Copyright: 3 golpes no mesmo alvo = removido por violacao de direitos autorais.
- Receitas usam itens do Create (shaft, casings, electron tube, precision mechanism, sheets...).

## Novidades desta versao

**Casca de banana 3D.** O Canhao de Banana agora deixa uma casca (bloco 3D com petalas) no chao onde acerta. Quem pisa
(andando/correndo) escorrega e e lancado para frente; agachar evita. Cada casca aguenta 3 escorregoes.
A casca tambem pode ser colocada a mao (item = bloco).

**Cyberware no corpo (visivel no seu personagem e nos outros jogadores).** Clique direito no implante para instalar
(custa 3 coracoes), Bisturi Cromado remove e devolve o item.
- Bracos de Gorila: +dano, +empurrao, +alcance, mineracao rapida, um pouco mais lento. Tecla **N** = pancada no chao.
- Laminas de Louva-a-Deus: tecla **G** estende/recolhe; estendidas dao +dano, +velocidade de ataque e alcance.
- Lancador de Projeteis (ombro): tecla **V** dispara mini-foguete (1 polvora). Explode sem quebrar blocos.
- Monofio (pulso): tecla **B** chicoteia em linha reta (9 blocos), fere, aplica definhar e puxa os alvos.
As teclas podem ser mudadas em Controles. Os implantes sao feitos em **Montagem Sequencial do Create**
(receitas em `data/bananacreate/recipe/sequenced/`).

**Ordem Paranormal + Create.**
- Sangue: Extrator e Centrifuga (ja existiam).
- Morte: **Sifao da Morte** (cinetico) puxa e drena monstros; quem morre vira Essencia de Morte.
- Energia: **Dinamo do Caos** (gerador cinetico) com RPM e sentido aleatorios; nos picos solta descargas.
- Medo: **Selo do Medo** (cinetico) afasta e enfraquece monstros num raio que cresce com o RPM.
- Conhecimento/essencias: **Haunting** (ventilador do Create sobre fogo da alma) transforma osso, ametista, olho de aranha
  e livro em Essencia de Morte, Fragmento de Caos, Essencia de Medo e Essencia de Conhecimento.

**Visual.** Texturas e modelos novos no estilo Create (casings com moldura e rebites, eixo atravessando as maquinas,
tanque de vidro de sangue). Os scripts `tools/gen_assets.py` e `tools/gen_models.py` regeneram tudo (Python + Pillow).

## Atualizacao 0.2.0 - Etapa 1: banana como base da progressao

- **Variacoes de banana:** Banana Verde (colhida cedo nas folhas), Banana (madura) e Banana Dourada (10% ao colher madura, ou banana + 2 chapas de ouro).
- **Liga de Banana:** bacia aquecida do Create (ferro + 2 bananas + 1 banana verde). Cabeca de todas as ferramentas.
- **Celula de Banana (a "energia"):** Montagem Sequencial sobre a Liga (chapa de cobre, banana, banana verde, prensa; 2 voltas). Quase toda receita pede Celula.
- **Nucleo de Banana:** 4 Celulas + 2 tubos de eletrons + 2 mecanismos de precisao + Banana Dourada. Pedido por Esteira de Tita, Braco Servo, Martelo do Copyright e implantes.
- **Ferramentas de banana:** Liga de Banana + Cabo de Bananeira (feito de tabuas). Texturas novas.
- **Implantes:** a Montagem Sequencial agora tem Nucleo, Celula e Banana Dourada alem do que ja tinha.
- **Casca de banana:** some assim que alguem escorrega nela.
- **Bananeira:** texturas novas de tronco, tabuas, folhas (verde/madura) e muda.
- **Tooltips:** todo item e bloco do addon explica o que faz (aparece tambem no JEI).
- Texturas novas: `python3 tools/gen_banana.py`.

## Atualizacao 0.3.0 - Etapa 2: Tita e Esteira (+ dash das laminas)

- **Transformacao em tita estilo AoT:** a Seringa paralisa o mob, raios caem (so visuais), ele infla ao longo de ~3,5 s com faiscas e batimentos,
  e explode em vapor, flash e onda de choque que empurra tudo em volta (nao quebra blocos). Sobrevive a recarregar o mundo.
- **Esteira de Tita:** prende UM tita por vez (o mais proximo, raio 6), sem IA, andando em circulo em cima dela: 32 RPM fixos.
  Matar o tita ou quebrar a esteira encerra a geracao; titas presos em outra esteira nao sao roubados.
- **Dash das Laminas (tecla Y):** teleporta ate a entidade na mira (max 15 blocos, sem paredes no caminho), corta ao chegar
  (10 de dano), estende as laminas sozinho. Recarga de 3 s.

## Atualizacao 0.3.1 - Etapa 3: Teto se defende

- **Kasane Teto agora revida.** Quando um ser vivo a machuca (jogador, mob, flecha...), ela para, canta (muda para a textura cantando)
  e dispara **rajadas sonoras** nele: carga de ~0,7 s com notas subindo de tom, depois uma linha de ondas sonicas e notas
  coloridas de ate 20 blocos. Cada rajada da 6 de dano (3 coracoes, ignora armadura), empurra e deixa o alvo tonto (Nausea).
  Dispara a cada ~1,7 s e continua por 10 s depois da ultima pancada (cada nova pancada renova o tempo). Para se o agressor morrer ou fugir.

## Atualizacao 0.4.0 - Etapa 4: mesas de implantes e gancho do Monofio

- **Implantes nao se instalam mais com botao direito.** Agora precisa da **Mesa de Implantes**: botao direito abre a interface, coloque o
  implante no slot e feche. Ele fica girando em 3D em cima da mesa; clique na mesa para instalar (custa 3 coracoes e consome o implante).
  Agachado + clique abre a interface de novo para trocar/pegar de volta. Quebrar a mesa dropa o implante.
- **Mesa de Remocao de Implantes:** botao direito abre a lista; clique num implante instalado para remove-lo (volta para o inventario, custa 1 coracao).
  O Bisturi Cromado continua funcionando.
- Receitas: Mesa de Implantes = 2 mecanismos de precisao + Nucleo de Banana + 2 Celulas + casing cromado + 2 ferro;
  Mesa de Remocao = igual, com Bisturi Cromado no lugar do Nucleo.
- **Gancho do Monofio (tecla R):** prende o fio no bloco da mira (ate 40 blocos) e te puxa ate la, como a teia do Homem-Aranha.
  R de novo ou agachar solta; ao chegar da um impulso para subir na borda. Fio visivel em particulas. Dura no maximo 6 s.
  (B continua sendo o chicote.)

## Atualizacao 0.5.0 - Etapa 5: JEI

- **Plugin do JEI** (`compat/BananaJeiPlugin`): todo item e bloco do addon ganha uma pagina de informacao no JEI (botao "i" ao lado do item,
  ou tecla U sobre ele) com o que faz e como obter; implantes mostram tambem a tecla e o aviso da Mesa de Implantes.
  O JEI entra no build como `compileOnly` (API) e `runtimeOnly` no `runClient`; no jogo ele continua opcional.
- Se o Gradle nao achar o JEI, confira a versao em `jei_version` (gradle.properties) para 1.21.1 em https://maven.blamejared.com/.

## Atualizacao 0.6.0 - Etapa 6: Mini Goblin novo

- **Modelo 3D proprio** (`client/MiniGoblinModel`), nao e mais um aldeao bebe: cabecona verde com olhos amarelos, nariz grande,
  orelhas pontudas que balancam, chapeu de folha com ponta mole, colete de couro com botoes de banana, calca roxa, botas
  e **mochila com uma banana pendurada**. Textura nova (`python3 tools/gen_goblin.py`).
- **Animacoes:** passinhos rapidos, orelhas e chapeu balancando, danca de verdade quando tem pao no chao (bracos pro alto, orelhas batendo).
- **Comportamento:** foge correndo quando apanha, segue quem segura Banana Dourada, voz de guincho (sons do Vex).
- Vida 16 (era 12).

## Atualizacao 0.7.0 - Etapas 7 e 8: bananeira nova + progressao longa e dificil

**Bananeira (etapa 7)**
- **Arvore alta** (7 a 11 blocos) com **copa de palmeira** (folhagem larga e achatada) e **cachos de banana pendurados**:
  folhas verdes = cacho verde, folhas maduras = cacho amarelo. Ja nasce com frutas em algumas folhas. Folhas sao
  vazadas (frondes), nao mais um cubo opaco.
- **Ferramentas redesenhadas:** picareta em arco de banana, machado de meia-lua, pa de casca aberta, enxada curva e espada
  curva com guarda de folha. Cada uma com silhueta propria (`python3 tools/gen_banana2.py`).
- **Poderes:** a Espada faz o alvo **escorregar** (20%: lancado para frente + lento). Picareta/Machado/Pa/Enxada soltam
  **Banana Verde** (6% por bloco).
- **Novas bananas:** Vermelha (12% nas folhas maduras, forca), Podre (Haunting), Cosmica (final) e Polpa de Banana (Moinho).
  Banana Dourada agora e 4% na colheita ou banana + 3 chapas de ouro.

**Progressao (etapa 8)** — a banana e a energia: nada importante sai sem Celula/Nucleo/Banana Cosmica.

| Tier | Item | Como fazer |
|---|---|---|
| 0 | Polpa de Banana | Moinho: banana madura = 2, verde = 1 |
| 1 | Liga de Banana (x2) | Bacia aquecida: ferro + **zinco** + 2 Polpas + Banana Verde |
| 1 | Placa Cromada | Montagem Sequencial sobre Chapa de Ferro: Polpa + prensa |
| 1 | Celula de Banana | Montagem Sequencial (2 voltas) sobre a Liga: chapa de cobre, Polpa, Banana Verde, prensa |
| 1 | Ferramentas, Canhao, Extratores, Centrifuga, Sifao, Selo, Dinamo | Liga e/ou Celulas; Sifao e Selo pedem **Banana Podre** |
| 2 | Nucleo de Banana | Montagem Sequencial (2 voltas) sobre Revestimento de Latao: Celula, Mecanismo de Precisao, **Banana Vermelha**, prensa |
| 2 | Esteira de Tita, Braco Servo, Martelo, Mesas de Implantes, Seringa, Injetor | Nucleo e/ou Banana Vermelha |
| 3 | Banana Cosmica | Montagem Sequencial sobre Banana Dourada: perola do fim + **as 4 essencias** + prensa |
| 3 | Implantes (4) | Chip Neural, Nucleo, Celula e **1 Banana Cosmica** cada |

Chip Neural agora exige Essencia de Conhecimento. O Mini Goblin vende atalhos caros (Placa, Banana Vermelha, Podre e Dourada).
As receitas ficam em `tools/gen_recipes.py` (rode de novo se quiser ajustar a dificuldade).
**Receitas novas do Create para conferir no build:** `create:milling` (polpa) e a montagem sequencial da Placa Cromada.

## Atualizacao 0.7.1 - Tema da transformacao em tita

- O audio `Voicy_Titan_transformation_theme.mp3` virou `sounds/titan_transformation.ogg` (8 s, mono, com fade-out) e toca no inicio da
  transformacao (`registry/ModSounds`, `sounds.json`, legenda traduzida). Audivel ate ~64 blocos.
- A transformacao agora dura **8 s** (`TitanTransformation.DURATION = 160`) para casar com a musica: raios aos 2 s, 4 s e 6 s,
  batimento mais baixo por baixo da musica, e a explosao cai quando a musica termina.
- Para outro trecho/duracao: `ffmpeg -i tema.mp3 -t 8 -af "afade=t=out:st=7.4:d=0.6" -ac 1 -ar 44100 -c:a libvorbis -q:a 4 titan_transformation.ogg`
  e ajuste `DURATION` (ticks = segundos x 20).
- **WIKI.md** na raiz: lista de tudo que existe no addon e o que faz.

## Pontos para conferir no primeiro build

O codigo foi escrito sem poder compilar. Se der erro, mande a mensagem completa. Os pontos mais provaveis:

1. `KineticBlockEntity.write/read` (assinatura com `HolderLookup.Provider`).
2. `lastCapacityProvided` em `TitanTreadmillBE` (campo protegido do Create).
3. Receitas `create:pressing` e `create:cutting` em `data/bananacreate/recipe/` (formato do Create 6).
4. Nome de itens do Create usados nas receitas (ex.: `create:brass_sheet`, `create:large_cogwheel`).
5. Receitas `create:haunting` e `create:sequenced_assembly` (formato do Create 6 / 1.21.1).
6. Camada do jogador: `EntityRenderersEvent.AddLayers` + `ModelPart.render(...)` e anexar pecas ao braco/tronco.
7. Menus/telas das mesas (`MenuType`, `RegisterMenuScreensEvent`) e `ImplantTableBE` (NBT do item, `ItemStack.parse`).
8. Dependencia do JEI (`jei_version`) e `addItemStackInfo` no plugin.
9. Pacotes de rede (`RegisterPayloadHandlersEvent`) e o anexo `ModAttachments.CYBERWARE`.

Limitacoes conhecidas: as maquinas nao tem animacao de rotacao (modelo estatico; animar exige Flywheel Visual) e o cyberware
nao aparece na mao em primeira pessoa.
