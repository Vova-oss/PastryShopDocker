package com.example.demo.ForRCSCH.Fixtures;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PlotOrientation;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.List;

@Controller
@RequestMapping("/fix")
public class FixtureController {

    String dirPath = System.getProperty("user.dir").replace('\\', '/') + "/src/main/resources/static/";
//    String dirPath = System.getProperty("user.dir").replace('\\', '/') + "static/";

    private List<FixturePerson> list;

    @GetMapping("/createGraph")
    public void createGraph(){
        // Построение графика в консоли
        //ГРАФИК ПИРОГ
        DefaultPieDataset dataset = new DefaultPieDataset();

        dataset.setValue("Мужчин", list.stream().filter(FixturePerson::getIsMan).count());
        dataset.setValue("Женщин", list.stream().filter(fixturePerson -> !fixturePerson.getIsMan()).count());
        JFreeChart pieObject = ChartFactory.createPieChart(
                "Статистика мужчин и женщин",
                dataset,
                true,
                true,
                false);
        int width =3840/1;
        int height = 2160/1;
        System.out.println();


        // Сохранение картинки графика
        File pieFile = new File(dirPath+"fixtures/pieChart.jpg");

        //ГРАФИК ЛИНЕЙНЫЙ
        DefaultCategoryDataset lineChart = new DefaultCategoryDataset();
        list.forEach(fixturePerson -> lineChart.addValue(fixturePerson.getAge(), "age", fixturePerson.getName()+" "+fixturePerson.getSurname()));

        JFreeChart lineObject = ChartFactory.createLineChart(
                "Age of Mans",
                "Names",
                "Ages",
                lineChart,
                PlotOrientation.VERTICAL,
                true,true,false
        );

        // Сохранение картинки графика
        File lineFile = new File(dirPath+"fixtures/lineChart.jpg");

        //Столбчатый график
        DefaultCategoryDataset histogram = new DefaultCategoryDataset();
        list.forEach(fixturePerson -> histogram.addValue(fixturePerson.getHeight(), fixturePerson.getName()+" "+fixturePerson.getSurname(), "Рост"));
        list.forEach(fixturePerson -> histogram.addValue(fixturePerson.getWeight(), fixturePerson.getName()+" "+fixturePerson.getSurname(), "Вес"));
        JFreeChart barObject = ChartFactory.createBarChart(
                "Статистика человеков",
                "Человеки",
                "Значения",
                histogram,
                PlotOrientation.VERTICAL,
                true, true, false);




        // Сохранение картинки графика
        File barFile = new File(dirPath + "fixtures/histogram.jpg");

        try {
            ChartUtilities.saveChartAsJPEG(lineFile, lineObject, width, height);

            width =3840/4;
            height = 2160/4;

            ChartUtilities.saveChartAsJPEG(pieFile, pieObject, width, height);
            ChartUtilities.saveChartAsJPEG(barFile, barObject, width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Наложение на графика водяного знака
        File dir = new File(dirPath + "fixtures/");
        for(File file: dir.listFiles()){

            try {
                ImageIcon image = new ImageIcon(file.getAbsolutePath());
                BufferedImage bufferedImage = new BufferedImage(
                        image.getIconWidth(),
                        image.getIconHeight(),
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
                g2d.drawImage(image.getImage(), 0, 0, null);

                AlphaComposite alpha =
                        AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f);
                g2d.setComposite(alpha);

                g2d.setColor(Color.white);
                g2d.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2d.setFont(new Font("Arial", Font.BOLD, 30));
                String copyright = "Vadim I Vova I Tёmochka(C) 2021";

                FontMetrics fontMetrics = g2d.getFontMetrics();
                Rectangle2D rect = fontMetrics.getStringBounds(copyright, g2d);
                g2d.drawString(copyright,
                        (image.getIconWidth() - (int) rect.getWidth()) / 2,
                        (image.getIconHeight() - (int) rect.getHeight()) / 2);

                g2d.dispose();

                OutputStream out = new PrintStream(file.getAbsolutePath());
                ImageIO.write(bufferedImage, "jpg", out);
                out.close();
            } catch (IOException ioe) {
                System.err.println(ioe);
            }

        }
    }

    public void setList(List<FixturePerson> list) {
        this.list = list;
    }
}
