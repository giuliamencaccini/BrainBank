package it.ispwproject.brainbank.controller.gui;

import it.ispwproject.brainbank.bean.StatisticsBean;
import it.ispwproject.brainbank.controller.applicativo.ReportStatisticsController;
import it.ispwproject.brainbank.exception.DAOException;
import it.ispwproject.brainbank.view.gui.ReportStatisticsGUIView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ReportStatisticsGUI {

    private final Stage                     stage;
    private final ReportStatisticsController controller = new ReportStatisticsController();
    private final ReportStatisticsGUIView       view       = new ReportStatisticsGUIView();

    public ReportStatisticsGUI(Stage stage) { this.stage = stage; }

    public void show() {
        BorderPane root = view.buildRoot(MainGUI::showDashboardAdmin);
        try {
            StatisticsBean stats = controller.getStatistics();
            root.setCenter(view.buildContent(stats));
        } catch (DAOException e) {
            root.setCenter(view.buildErrorContent(e.getMessage()));
        }
        stage.setScene(GUIUtils.createScene(root));
        stage.show();
    }
}