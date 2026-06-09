package br.com.fiap.space.presentation;

import br.com.fiap.space.application.MissaoService;
import br.com.fiap.space.domain.exception.BateriaCriticaException;
import br.com.fiap.space.domain.exception.CargaExcedidaException;
import br.com.fiap.space.domain.exception.TerrenoInvalidoException;
import br.com.fiap.space.domain.model.Recurso;
import br.com.fiap.space.domain.model.Sonda;
import br.com.fiap.space.domain.model.Terreno;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static MissaoService missaoService = new MissaoService();

    public static void main(String[] args) {
        exibirBanner();

        int opcao = -1;

        while (opcao != 0) {
            exibirMenuPrincipal();
            opcao = lerOpcaoNumerica();

            switch (opcao) {
                case 1:
                    lancarSonda();
                    break;
                case 2:
                    listarSondas();
                    break;
                case 3:
                    executarMissao();
                    break;
                case 4:
                    recarregarSonda();
                    break;
                case 5:
                    exibirRelatorios();
                    break;
                case 0:
                    System.out.println("\n[Sistema] Encerrando SAFS... Ate a proxima missao, Comandante!");
                    break;
                default:
                    System.out.println("\n[Alerta] Opcao invalida. Selecione uma opcao do menu.");
                    break;
            }
        }

        scanner.close();
    }

    private static void exibirBanner() {
        System.out.println("=============================================================");
        System.out.println("     SAFS - Surface Autonomous Fleet System                  ");
        System.out.println("     Centro de Missao - Base Artemis Alpha                   ");
        System.out.println("     Global Solution FIAP 2026 | Domain-Driven Design        ");
        System.out.println("=============================================================");
        System.out.println();
    }

    private static void exibirMenuPrincipal() {
        System.out.println("\n+--------------------------------------------+");
        System.out.println("|            MENU PRINCIPAL - SAFS           |");
        System.out.println("+--------------------------------------------+");
        System.out.println("|  1. Lancar Nova Sonda                      |");
        System.out.println("|  2. Listar Sondas Ativas                   |");
        System.out.println("|  3. Executar Missao (Atribuir Coordenada)  |");
        System.out.println("|  4. Recarregar Sonda na Base               |");
        System.out.println("|  5. Exibir Relatorios da Missao            |");
        System.out.println("|  0. Encerrar Sistema                       |");
        System.out.println("+--------------------------------------------+");
        System.out.print("Comandante, selecione uma opcao: ");
    }

    // === CASO DE USO 1: LANCAR SONDA ===
    private static void lancarSonda() {
        System.out.println("\n--- LANCAMENTO DE NOVA SONDA ---");
        System.out.println("Tipo de Sonda:");
        System.out.println("  1. MINERACAO  (SondaMineradora - extrai recursos)");
        System.out.println("  2. EXPLORACAO (SondaExploradora - mapeia terreno)");
        System.out.print("Escolha: ");

        String tipoInput = scanner.nextLine().trim();
        String tipo;
        Recurso recurso = null;

        if (tipoInput.equals("1")) {
            tipo = "MINERACAO";
            recurso = selecionarRecurso();
        } else if (tipoInput.equals("2")) {
            tipo = "EXPLORACAO";
        } else {
            System.out.println("[Alerta] Tipo invalido. Operacao cancelada.");
            return;
        }

        try {
            Sonda sonda = missaoService.lancarSonda(tipo, recurso);
            System.out.println("\n[Sucesso] Nova sonda lancada:");
            System.out.println("  " + sonda);
        } catch (IllegalArgumentException e) {
            System.out.println("[Erro] " + e.getMessage());
        }
    }

    private static Recurso selecionarRecurso() {
        System.out.println("\nRecurso Alvo para Mineracao:");

        Recurso[] recursos = Recurso.values();
        for (int i = 0; i < recursos.length; i++) {
            System.out.printf("  %d. %s (peso: %.1f kg/L)%n",
                i + 1, recursos[i].getNome(), recursos[i].getPesoPorUnidade());
        }
        System.out.print("Escolha: ");

        int escolha = lerOpcaoNumerica();
        int idx = escolha - 1;

        if (idx >= 0 && idx < recursos.length) {
            return recursos[idx];
        }

        System.out.println("[Aviso] Selecao invalida. Usando GELO como recurso padrao.");
        return Recurso.GELO;
    }

    // === CASO DE USO 2: LISTAR SONDAS ===
    private static void listarSondas() {
        System.out.println("\n--- SONDAS ATIVAS NA MISSAO ---");

        List<Sonda> sondas = missaoService.listarSondas();

        if (sondas.isEmpty()) {
            System.out.println("Nenhuma sonda registrada. Lance uma sonda primeiro.");
            return;
        }

        System.out.println("--------------------------------------------------------------------------------");
        for (int i = 0; i < sondas.size(); i++) {
            System.out.println((i + 1) + ". " + sondas.get(i));
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.println("Total: " + sondas.size() + " sonda(s) ativa(s).");
    }

    // === CASO DE USO 3: EXECUTAR MISSAO ===
    private static void executarMissao() {
        System.out.println("\n--- ATRIBUICAO DE MISSAO ---");

        List<Sonda> sondas = missaoService.listarSondas();
        if (sondas.isEmpty()) {
            System.out.println("[Alerta] Nenhuma sonda disponivel. Lance uma sonda primeiro.");
            return;
        }

        listarSondas();

        System.out.print("\nID da Sonda alvo (ex: SND-001): ");
        String idSonda = scanner.nextLine().trim().toUpperCase();

        if (!existeSondaComId(sondas, idSonda)) {
            System.out.println("[Erro] Sonda com ID '" + idSonda + "' nao encontrada.");
            return;
        }

        System.out.print("Coordenada X (0 a 100): ");
        int x = lerOpcaoNumerica();

        System.out.print("Coordenada Y (0 a 100): ");
        int y = lerOpcaoNumerica();

        if (x < 0 || x > 100 || y < 0 || y > 100) {
            System.out.println("[Erro] Coordenadas devem estar entre 0 e 100.");
            return;
        }

        Terreno terreno = selecionarTerreno();
        if (terreno == null) {
            return;
        }

        try {
            missaoService.executarMissao(idSonda, x, y, terreno);
            System.out.println("\n[Missao Concluida] Relatorio registrado no Centro de Comando.");
        } catch (BateriaCriticaException e) {
            System.out.println("\n*** ALERTA: BATERIA CRITICA! ***");
            System.out.println("Detalhe: " + e.getMessage());
            System.out.println("Acao recomendada: Recarregue a sonda antes de prosseguir (opcao 4).");
        } catch (CargaExcedidaException e) {
            System.out.println("\n*** ALERTA: CAPACIDADE DE CARGA EXCEDIDA! ***");
            System.out.println("Detalhe: " + e.getMessage());
            System.out.println("Acao recomendada: Retorne a base para descarregar os recursos (opcao 4).");
        } catch (TerrenoInvalidoException e) {
            System.out.println("\n*** ALERTA: TERRENO INVALIDO PARA ESTE TIPO DE SONDA! ***");
            System.out.println("Detalhe: " + e.getMessage());
            System.out.println("Acao recomendada: Selecione outro terreno ou use uma Sonda Exploradora.");
        } catch (IllegalArgumentException e) {
            System.out.println("[Erro] " + e.getMessage());
        }
    }

    private static Terreno selecionarTerreno() {
        System.out.println("\nTipo de Terreno de Destino:");

        Terreno[] terrenos = Terreno.values();
        for (int i = 0; i < terrenos.length; i++) {
            if (terrenos[i].isRestritoPorRodas()) {
                System.out.printf("  %d. %-15s (consumo x%.1f) [RESTRITO para sondas mineradoras]%n",
                    i + 1, terrenos[i].getTipoSolo(), terrenos[i].getMultiplicadorConsumo());
            } else {
                System.out.printf("  %d. %-15s (consumo x%.1f)%n",
                    i + 1, terrenos[i].getTipoSolo(), terrenos[i].getMultiplicadorConsumo());
            }
        }
        System.out.print("Escolha: ");

        int escolha = lerOpcaoNumerica();
        int idx = escolha - 1;

        if (idx >= 0 && idx < terrenos.length) {
            return terrenos[idx];
        }

        System.out.println("[Erro] Terreno invalido. Operacao cancelada.");
        return null;
    }

    // === CASO DE USO 4: RECARREGAR SONDA ===
    private static void recarregarSonda() {
        System.out.println("\n--- RECARGA DE SONDA NA BASE ---");

        List<Sonda> sondas = missaoService.listarSondas();
        if (sondas.isEmpty()) {
            System.out.println("[Alerta] Nenhuma sonda registrada.");
            return;
        }

        listarSondas();

        System.out.print("\nID da Sonda para recarregar (ex: SND-001): ");
        String idSonda = scanner.nextLine().trim().toUpperCase();

        if (!existeSondaComId(sondas, idSonda)) {
            System.out.println("[Erro] Sonda com ID '" + idSonda + "' nao encontrada.");
            return;
        }

        missaoService.recarregarSonda(idSonda);
    }

    private static boolean existeSondaComId(List<Sonda> sondas, String idSonda) {
        for (Sonda sonda : sondas) {
            if (sonda.getIdSonda().equals(idSonda)) {
                return true;
            }
        }
        return false;
    }

    // === CASO DE USO 5: EXIBIR RELATORIOS ===
    private static void exibirRelatorios() {
        System.out.println("\n--- RELATORIOS DO CENTRO DE COMANDO ---");

        List<String> relatorios = missaoService.obterRelatorios();

        if (relatorios.isEmpty()) {
            System.out.println("Nenhum relatorio disponivel. Execute missoes para gerar relatorios.");
            return;
        }

        System.out.println("================================================================================");
        for (int i = 0; i < relatorios.size(); i++) {
            System.out.println(relatorios.get(i));
        }
        System.out.println("================================================================================");
        System.out.println("Total: " + relatorios.size() + " relatorio(s) registrado(s).");
    }

    private static int lerOpcaoNumerica() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
