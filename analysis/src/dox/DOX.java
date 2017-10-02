package dox;

import investment_simulator.report.SimulationReport;
import repository.stock_repository.StockRepository;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.File;
import java.time.LocalDate;
import java.util.*;

public abstract class DOX {
    public final StockRepository repository;

    private LinkedList<DesignAxis> designAxis;

    private LocalDate startDate;
    private LocalDate endDate;

    public DOX(StockRepository repository) {
        this.repository = repository;
        designAxis = new LinkedList<>();
        startDate = LocalDate.MIN;
        endDate = LocalDate.MAX;
    }

    public DOX(StockRepository repository, LocalDate startDate, LocalDate endDate) {
        this.repository = repository;
        designAxis = new LinkedList<>();
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    protected abstract DOXReport getDoxReport(File doxReportDirectory);

    public void simulate(File reportDirectory) throws Exception {
        DOXReport doxReport = getDoxReport(reportDirectory);
        for (List<Double> axisValues : calculatePossibleAxisValues()) {
            tryProcessingSimulation(doxReport, axisValues);
        }
    }

    private Iterable<? extends List<Double>> calculatePossibleAxisValues() {
        LinkedList<List<Double>> inputValues = new LinkedList<>();
        for (DesignAxis designAxi : designAxis) {
            inputValues.add(designAxi.calculateValues());
        }
        List<DefaultMutableTreeNode> values = buildTreeNodes(inputValues);
        return calculatePossibleAxisValues(values);
    }

    private List<DefaultMutableTreeNode> buildTreeNodes(LinkedList<List<Double>> inputValueStack) {
        if (inputValueStack.size() == 0) {
            return Collections.emptyList();
        }

        List<DefaultMutableTreeNode> treeNodes = new LinkedList<>();
        for (Double inputValue : inputValueStack.pop()) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(inputValue);
            buildTreeNodes((LinkedList<List<Double>>) inputValueStack.clone()).forEach(node::add);
            treeNodes.add(node);
        }
        return treeNodes;
    }

    private Iterable<? extends List<Double>> calculatePossibleAxisValues(List<DefaultMutableTreeNode> values) {
        LinkedList<List<Double>> possibleValues = new LinkedList<>();
        for (DefaultMutableTreeNode treeNode : values) {
            possibleValues.addAll(calculatePossibleAxisValues(treeNode));
        }
        return possibleValues;
    }

    private Collection<? extends List<Double>> calculatePossibleAxisValues(DefaultMutableTreeNode treeNode) {
        Enumeration<DefaultMutableTreeNode> leafs = treeNode.depthFirstEnumeration();
        LinkedList<List<Double>> possibleValues = new LinkedList<>();
        while (leafs.hasMoreElements()) {
            DefaultMutableTreeNode node = leafs.nextElement();
            if (node.isLeaf()) {
                possibleValues.add(buildValueList(node.getUserObjectPath()));
            }
        }
        return possibleValues;
    }

    private List<Double> buildValueList(Object[] userObjectPath) {
        List<Double> values = new ArrayList<>(userObjectPath.length);
        for (Object valueObject : userObjectPath) {
            values.add((Double) valueObject);
        }
        return values;
    }

    private void tryProcessingSimulation(DOXReport doxReport, List<Double> axisValues) throws Exception {
        try {
            processSimulation(doxReport, axisValues);
        } catch (Exception e) {
            doxReport.processSimulationException(e);
        }
    }

    private void processSimulation(DOXReport doxReport, List<Double> axisValues) throws Exception {
        SimulationReport simulationReport = simulateExperiment(axisValues);
        doxReport.processReport(simulationReport, axisValues);
    }

    private SimulationReport simulateExperiment(List<Double> axisValues) {
        Experiment experiment = getExperiment();
        experiment.setStartDate(startDate);
        experiment.setEndDate(endDate);
        return experiment.simulate(axisValues);
    }

    protected abstract Experiment getExperiment();

    protected LinkedList<DesignAxis> getDesignAxis() {
        return designAxis;
    }
}
