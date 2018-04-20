import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;



public class Scheduler extends Application {

	// variabelen
	Process p;
	int pid;
	int at;
	int st;
	int aantalProc = 10000;
	int percentiel = aantalProc / 100;

	@Override
	public void start(Stage stage) throws Exception {
		// stage aanmaken
		Stage stage2 = new Stage();
		stage.setTitle("Scheduler");

		// defining the axes
		final NumberAxis xAxis = new NumberAxis(0, 100, 5);
		final LogarithmicAxis yAxis = new LogarithmicAxis(0.9, 500);
		yAxis.setLabel("genormaliseerde TAT");
		xAxis.setLabel("bedieningstijd in percentiel");
		//final NumberAxis yAxis = new NumberAxis(0, 600, 5);

		final NumberAxis xAxis2 = new NumberAxis(0, 100, 5);
		final NumberAxis yAxis2 = new NumberAxis(0, 2600, 5);
		yAxis2.setLabel("wachttijd");
		xAxis2.setLabel("bedieningstijd in percentiel");
		// final LogarithmicAxis yAxis2 = new LogarithmicAxis(0.01, 30000);

		// creating the chart
		final LineChart<Number, Number> lineChart1 = new LineChart<Number, Number>(xAxis, yAxis);
		final LineChart<Number, Number> lineChart2 = new LineChart<Number, Number>(xAxis2, yAxis2);
		lineChart1.setTitle("genormalizeerde TAT");
		lineChart2.setTitle("Wachttijd");

		// FCFS
		XYChart.Series series1 = new XYChart.Series();
		series1.setName("FCFS: genormalizeerde TAT ifv percentiel");
		XYChart.Series series2 = new XYChart.Series();
		series2.setName("FCFS: Wachttijd ifv percentiel");

		// SJF
		XYChart.Series series3 = new XYChart.Series();
		series3.setName("SJF: genormalizeerde TAT ifv percentiel");
		XYChart.Series series4 = new XYChart.Series();
		series4.setName("SJF: Wachttijd ifv percentiel");

		// SRT
		XYChart.Series series5 = new XYChart.Series();
		series5.setName("SRT: genormalizeerde TAT ifv percentiel");
		XYChart.Series series6 = new XYChart.Series();
		series6.setName("SRT: Wachttijd ifv percentiel");

		// RR
		XYChart.Series series7 = new XYChart.Series();
		series7.setName("RR q=2: genormalizeerde TAT ifv percentiel");
		XYChart.Series series8 = new XYChart.Series();
		series8.setName("RR q=2: Wachttijd ifv percentiel");
		XYChart.Series series9 = new XYChart.Series();
		series9.setName("RR q=8: genormalizeerde TAT ifv percentiel");
		XYChart.Series series10 = new XYChart.Series();
		series10.setName("RR q=8: Wachttijd ifv percentiel");

		XYChart.Series series11 = new XYChart.Series();
		series11.setName("HRRN: genormalizeerde TAT ifv percentiel");
		XYChart.Series series12 = new XYChart.Series();
		series12.setName("HRRN: Wachttijd ifv percentiel");

		XYChart.Series series13 = new XYChart.Series();
		series13.setName("FB: genormalizeerde TAT ifv percentiel");
		XYChart.Series series14 = new XYChart.Series();
		series14.setName("FB: Wachttijd ifv percentiel");

		Algoritme fcfs = new Algoritme(aantalProc, 0);
		Algoritme sjn = new Algoritme(aantalProc, 0);
		Algoritme srt = new Algoritme(aantalProc, 0);
		Algoritme rr2 = new Algoritme(aantalProc, 2);
		Algoritme rr8 = new Algoritme(aantalProc, 8);
		Algoritme hrrn = new Algoritme(aantalProc, 0);
		Algoritme fb = new Algoritme(aantalProc, 0);
		try {

			File fXmlFile = new File("processen" + (int) aantalProc + ".xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			// optional, but recommended
			// read this -
			// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
			doc.getDocumentElement().normalize();

			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			NodeList nList = doc.getElementsByTagName("process");

			System.out.println("----------------------------");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					pid = Integer.parseInt(eElement.getElementsByTagName("pid").item(0).getTextContent());
					at = Integer.parseInt(eElement.getElementsByTagName("arrivaltime").item(0).getTextContent());
					st = Integer.parseInt(eElement.getElementsByTagName("servicetime").item(0).getTextContent());

					fcfs.inlezenFCFS(at, pid, st);
					sjn.inlezenSJN(at, pid, st, temp);
					srt.inlezenAT(at, pid, st);
					rr2.inlezenAT(at, pid, st);
					rr8.inlezenAT(at, pid, st);
					hrrn.inlezenHRRN(at, pid, st);
					fb.inlezenAT(at, pid, st);

				}
			}
			sjn.SJN();
			srt.SRT();
			rr2.RR();
			rr8.RR();
			hrrn.HRRN();
			fb.FB();
System.out.println(fb.getProcessenLijst().size());
			int i = 1;
			while (fcfs.getProcessenLijst().peek() != null) {

				// berekenen gemiddeldes voor SRT en RR
				srt.berekenGemiddelde();
				rr2.berekenGemiddelde();
				rr8.berekenGemiddelde();
				fb.berekenGemiddelde();

				// berekenen grafieken
				fcfs.berekenGrafieken();
				sjn.berekenGrafieken();
				srt.berekenGrafieken();
				rr2.berekenGrafieken();
				rr8.berekenGrafieken();
				hrrn.berekenGrafieken();
				fb.berekenGrafieken();

				if (i % (percentiel) == 0) {
					series1.getData().add(new XYChart.Data(i / percentiel, fcfs.getNormOmloopPPC() / percentiel));
					series2.getData().add(new XYChart.Data(i / percentiel, fcfs.getWachtPPC() / percentiel));
					series3.getData().add(new XYChart.Data(i / percentiel, sjn.getNormOmloopPPC() / percentiel));
					series4.getData().add(new XYChart.Data(i / percentiel, sjn.getWachtPPC() / percentiel));
					series5.getData().add(new XYChart.Data(i / percentiel, srt.getNormOmloopPPC() / percentiel));
					series6.getData().add(new XYChart.Data(i / percentiel, srt.getWachtPPC() / percentiel));
					series7.getData().add(new XYChart.Data(i / percentiel, rr2.getNormOmloopPPC() / percentiel));
					series8.getData().add(new XYChart.Data(i / percentiel, rr2.getWachtPPC() / percentiel));
					series9.getData().add(new XYChart.Data(i / percentiel, rr8.getNormOmloopPPC() / percentiel));
					series10.getData().add(new XYChart.Data(i / percentiel, rr8.getWachtPPC() / percentiel));
					series11.getData().add(new XYChart.Data(i / percentiel, hrrn.getNormOmloopPPC() / percentiel));
					series12.getData().add(new XYChart.Data(i / percentiel, hrrn.getWachtPPC() / percentiel));
					series13.getData().add(new XYChart.Data(i / percentiel, hrrn.getNormOmloopPPC() / percentiel));
					series14.getData().add(new XYChart.Data(i / percentiel, hrrn.getWachtPPC() / percentiel));

					// resetten waardes
					fcfs.resetPPC();
					sjn.resetPPC();
					srt.resetPPC();
					rr2.resetPPC();
					rr8.resetPPC();
					hrrn.resetPPC();
					fb.resetPPC();

				}

				i++;
			}

			System.out.println("FCFS:");
			System.out.println("gemOmloop " + fcfs.getGemNormOmloop() / aantalProc);
			System.out.println("gemNormOmloop " + fcfs.getGemOmloop() / aantalProc);
			System.out.println("gemWacht " + fcfs.getGemWacht() / aantalProc);

			System.out.println("SJF:");
			System.out.println("gemOmloop " + sjn.getGemNormOmloop() / aantalProc);
			System.out.println("gemNormOmloop " + sjn.getGemOmloop() / aantalProc);
			System.out.println("gemWacht " + sjn.getGemWacht() / aantalProc);

			System.out.println("SRT:");
			System.out.println("gemOmloop " + srt.getGemNormOmloop() / aantalProc);
			System.out.println("gemNormOmloop " + srt.getGemOmloop() / aantalProc);
			System.out.println("gemWacht " + srt.getGemWacht() / aantalProc);

			System.out.println("RR q=2:");
			System.out.println("gemOmloop " + rr2.getGemNormOmloop() / aantalProc);
			System.out.println("gemNormOmloop " + rr2.getGemNormOmloop() / aantalProc);
			System.out.println("gemWacht " + rr2.getGemNormOmloop() / aantalProc);

			System.out.println("RR q=8:");
			System.out.println("gemOmloop " + rr8.getGemNormOmloop() / aantalProc);
			System.out.println("gemNormOmloop " + rr8.getGemOmloop() / aantalProc);
			System.out.println("gemWacht " + rr8.getGemWacht() / aantalProc);

			System.out.println("HRRN");
			System.out.println("gemOmloop " + hrrn.getGemNormOmloop() / aantalProc);
			System.out.println("gemNormOmloop " + hrrn.getGemOmloop() / aantalProc);
			System.out.println("gemWacht " + hrrn.getGemWacht() / aantalProc);

			System.out.println("FB");
			System.out.println("gemOmloop " + fb.getGemNormOmloop() / aantalProc);
			System.out.println("gemNormOmloop " + fb.getGemOmloop() / aantalProc);
			System.out.println("gemWacht " + fb.getGemWacht() / aantalProc);
			Scene scene = new Scene(lineChart1, 800, 1000);
			Scene scene2 = new Scene(lineChart2, 800, 1000);
			lineChart1.getData().addAll(series1, series3, series5, series7, series9, series11, series13);
			lineChart2.getData().addAll(series2, series4, series6, series8, series10, series12, series14);
			lineChart1.setCreateSymbols(false);
			lineChart2.setCreateSymbols(false);
			stage.setScene(scene);
			stage2.setScene(scene2);
			stage.setMaximized(true);
			stage2.setMaximized(true);

			stage.show();
			stage2.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		launch(args);

	}

}
