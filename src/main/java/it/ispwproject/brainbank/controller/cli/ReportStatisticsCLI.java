package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.bean.StatisticsBean;
import it.ispwproject.brainbank.controller.applicativo.ReportStatisticsController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.cli.ReportStatisticsView;

public class ReportStatisticsCLI {

    private final ReportStatisticsController controller = new ReportStatisticsController();
    private final ReportStatisticsView view = new ReportStatisticsView();

    public CLIState start() {
        view.mostraIntestazione();
        try {
            StatisticsBean stats = controller.getStatistics();
            view.mostraStatistiche(stats);
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        return CLIState.DASHBOARD_ADMIN;
    }
}