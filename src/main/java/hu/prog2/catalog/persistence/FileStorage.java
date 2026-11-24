package hu.prog2.catalog.persistence;

import hu.prog2.catalog.domain.ElectronicProduct;
import hu.prog2.catalog.domain.FoodProduct;
import hu.prog2.catalog.domain.InvalidProductException;
import hu.prog2.catalog.domain.Product;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class FileStorage {

    public void saveToFile(ArrayList<Product> products, String fileName) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Product p : products) {
                if (p instanceof ElectronicProduct) {
                    ElectronicProduct ep = (ElectronicProduct) p;
                    writer.write("E;" + ep.getId() + ";" + ep.getName() + ";" + ep.getPrice() + ";" + ep.getStock() + ";" + ep.getWarrantyMonths());
                } else if (p instanceof FoodProduct) {
                    FoodProduct fp = (FoodProduct) p;
                    writer.write("F;" + fp.getId() + ";" + fp.getName() + ";" + fp.getPrice() + ";" + fp.getStock() + ";" + fp.getShelfLifeDays());
                }
                writer.newLine();
            }
        }
    }

    public ArrayList<Product> loadFromFile(String fileName) throws IOException, InvalidProductException {
        ArrayList<Product> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(";");
                String type = parts[0];
                int id = Integer.parseInt(parts[1]);
                String name = parts[2];
                double price = Double.parseDouble(parts[3]);
                int stock = Integer.parseInt(parts[4]);
                int extra = Integer.parseInt(parts[5]);

                if (type.equals("E")) {
                    list.add(new ElectronicProduct(id, name, price, stock, extra));
                } else if (type.equals("F")) {
                    list.add(new FoodProduct(id, name, price, stock, extra));
                }
            }
        }
        return list;
    }
}
