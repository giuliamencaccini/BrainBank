package it.ispwproject.brainbank.controller.cli;

import it.ispwproject.brainbank.pattern.state.AbstractCLIState;
import it.ispwproject.brainbank.pattern.state.CLIStateMachine;

import it.ispwproject.brainbank.bean.StatisticsBean;
import it.ispwproject.brainbank.controller.applicativo.ReportStatisticsController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.cli.ReportStatisticsView;

public class ReportStatisticsCLI extends AbstractCLIState {

    private final ReportStatisticsController controller = new ReportStatisticsController();
    private final ReportStatisticsView view = new ReportStatisticsView();

    @Override
    public void entry(CLIStateMachine context) {
        view.mostraIntestazione();
    }

    @Override
    public void action(CLIStateMachine context) {
        try {
            StatisticsBean stats = controller.getStatistics();
            view.mostraStatistiche(stats);
        } catch (DAOException e) {
            view.mostraErrore(e.getMessage());
        }
        goBack(context);
    }
}