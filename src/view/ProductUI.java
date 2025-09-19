package view;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

import business.ProductController;
import core.Helper;
import entity.Product;

public class ProductUI extends JFrame {

    private JPanel container;
    private ProductController productController;
    private Product product;

    private JTextField fldName;
    private JTextField fldCode;
    private JTextField fldPrice;  // BigDecimal string olarak girilecek
    private JTextField fldStock;  // int

    private JButton btnSave;

    public ProductUI(Product product) {
        this.product = product;
        this.productController = new ProductController();

        container = new JPanel(new BorderLayout());
        setContentPane(container);
        setTitle("Product Add/Update");
        setSize(400, 600);
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - getSize().height) / 2;
        setLocation(x, y);

        // Orta form paneli (GridBag)
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        container.add(form, BorderLayout.CENTER);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.LINE_START;
        gc.weightx = 1;

        int row = 0;

        // Başlık
        JLabel lblTitle = new JLabel(product.getId() == 0 ? "Product Add" : "Product Update");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(lblTitle, gc);
        gc.gridwidth = 1;
        row++;

        // Name
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Name"), gc);
        fldName = new JTextField();
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(fldName, gc);

        // Code
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Code"), gc);
        fldCode = new JTextField();
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(fldCode, gc);

        // Price
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Price"), gc);
        fldPrice = new JTextField();
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(fldPrice, gc);

        // Stock
        gc.gridx = 0; gc.gridy = row; gc.weightx = 0; gc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Stock"), gc);
        fldStock = new JTextField();
        gc.gridx = 1; gc.gridy = row++; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;
        form.add(fldStock, gc);

        // Save
        btnSave = new JButton("Save");
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 2;
        gc.weightx = 0; gc.weighty = 0; gc.fill = GridBagConstraints.NONE; gc.anchor = GridBagConstraints.CENTER;
        form.add(btnSave, gc);

        // Edit modunda alanları doldur
        if (product.getId() > 0) {
            fldName.setText(product.getName());
            fldCode.setText(product.getCode());
            fldPrice.setText(product.getPrice() != null ? product.getPrice().toPlainString() : "");
            fldStock.setText(String.valueOf(product.getStock()));
        }

        // Kaydet davranışı
        btnSave.addActionListener(e -> {
            JTextField[] checklist = {fldName, fldCode, fldPrice, fldStock};
            
            if(Helper.isFieldListEmpty(checklist)) {
            	Helper.showMessage("fill");
            }else {
            	BigDecimal price = parsePrice(fldPrice.getText());
                if (price == null || price.signum() < 0) {
                    Helper.showMessage("Please enter a valid price (e.g. 199.99)");
                    return;
                }

                Integer stock = parseInt(fldStock.getText());
                if (stock == null || stock < 0) {
                    Helper.showMessage("Please enter a valid non-negative stock");
                    return;
                }
            	// Formdan modele
            	product.setName(fldName.getText().trim());
                product.setCode(fldCode.getText().trim());
                product.setPrice(price);
                product.setStock(stock);
                
                boolean result;
                if (product.getId() == 0) {
                    result = productController.save(product);
                } else {
                    result = productController.update(product);
                }

                Helper.showMessage(result ? "done" : "error");
                if (result) dispose();
            }
            
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    private boolean isEmpty(JTextField f) {
        return f.getText() == null || f.getText().trim().isEmpty();
    }

    private BigDecimal parsePrice(String s) {
        try {
            // Kullanıcı virgülle yazarsa dönüştür (örn. 199,99)
            return new BigDecimal(s.trim().replace(',', '.'));
        } catch (Exception ex) {
            return null;
        }
    }

    private Integer parseInt(String s) {
        try {
            if (s == null || s.trim().isEmpty()) return 0; // boş bırakılırsa 0 kabul
            return Integer.parseInt(s.trim());
        } catch (Exception ex) {
            return null;
        }
    }
}