package org.example;


import org.example.models.Category;
import org.example.models.Photo;
import org.example.models.Product;
import org.example.utils.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Main {
    private static Scanner scan = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        Menu();
    }
    static String pathForImagesCategories="D:\\SHAG\\Java\\CW\\2\\CW_2\\src\\main\\images_categories\\";
    static String pathForImagesProducts="D:\\SHAG\\Java\\CW\\2\\CW_2\\src\\main\\images_products\\";
    private static void Menu() throws IOException {
        System.out.println("Привіт JAVA");
        int action = 0;
        do{
            System.out.println("0 - Вихід");
            System.out.println("1 - Додати");
            System.out.println("2 - Показати все");
            System.out.println("3 - Оновити");
            System.out.println("4 - Видалити");
            System.out.println("5 - Додати продукт");
            System.out.println("6 - Показати всі продукти");
            System.out.println("7 - Редагувати продукт");
            System.out.println("8 - Видалити продукт");
            System.out.println("-> ");
            action=Integer.valueOf(scan.nextLine());
            switch (action){
                case 1:
                    AddCategory();
                    break;
                case 2:
                    ShowCategories();
                    break;
                case 3:
                    EditCategory();
                    break;
                case 4:
                    DeleteCategory();
                    break;
                case 5:
                    AddProduct();
                    break;
                case 6:
                    ShowProducts();
                    break;
                case 7:
                    EditProduct();
                    break;
                case 8:
                    DeleteProduct();
                    break;
            }
        }while(action!=0);
    }
    private static void AddCategory() throws IOException {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try(Session context = sf.openSession()){
            context.beginTransaction();
            Category category = new Category();

            System.out.println("Введіть ім'я");
            String name = scan.nextLine();
            category.setName(name);

            System.out.println("Введіть посилання на фото (URL)");
            String _url = scan.nextLine();
            URL url = new URL(_url);
            BufferedImage image = ImageIO.read(url);
            String imageName = UUID.randomUUID().toString()+".jpg";
            Path path = Paths.get(pathForImagesCategories+imageName);
            ImageIO.write(image,"jpg",path.toFile());
            category.setImage(imageName);

            Calendar calendar = Calendar.getInstance();
            category.setDateCreated(calendar.getTime());
            context.save(category);
            context.getTransaction().commit();
        }
    }
    private static void ShowCategories(){
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try(Session context = sf.openSession()){
            context.beginTransaction();
            List<Category> list = context.createQuery("from Category",Category.class).getResultList();
            for(var item : list){
                System.out.println("#"+item.getId()+" | "+item.getName()+" | "+item.getDateCreated()+" | "+ item.getImage());
            }
            context.getTransaction().commit();
        }
    }
    private static void EditCategory() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try (Session context = sf.openSession()) {
            context.beginTransaction();
            System.out.println("Введіть id");
            int editId = Integer.valueOf(scan.nextLine());
            Category editCategory = context.createQuery("from Category where id="+editId, Category.class).getSingleResult();
            editCategory.setId(editId);
            System.out.println("Введіть нове ім'я");
            String name = scan.nextLine();
            editCategory.setName(name);

            System.out.println("Введіть нове посилання на фото (URL)");


            String _url = scan.nextLine();
            URL url = new URL(_url);
            Path deleteImageFromFile = Paths.get(pathForImagesCategories+editCategory.getImage());
            if(!Files.exists(deleteImageFromFile)) System.out.println("Картинки не існує");
            else Files.delete(deleteImageFromFile);
            BufferedImage image = ImageIO.read(url);
            String imageName = UUID.randomUUID().toString()+".jpg";
            Path path = Paths.get(pathForImagesCategories+imageName);
            ImageIO.write(image,"jpg",path.toFile());
            editCategory.setImage(imageName);

            Calendar calendar = Calendar.getInstance();
            editCategory.setDateCreated(calendar.getTime());
            context.update(editCategory);
            context.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
        private static void DeleteCategory(){
            SessionFactory sf = HibernateUtil.getSessionFactory();
            try(Session context = sf.openSession()) {
                context.beginTransaction();
                System.out.println("Введіть id");
                int deleteId=Integer.valueOf(scan.nextLine());
                Category deleteCategory = context.createQuery("from Category where id="+deleteId, Category.class).getSingleResult();
                List<Product> prod = context.createQuery("from Product where category.id="+deleteId, Product.class).getResultList();
                context.remove(prod);
                Path deleteImageFromFile = Paths.get(pathForImagesCategories+deleteCategory.getImage());
                if(!Files.exists(deleteImageFromFile)) System.out.println("Картинки не існує");
                else Files.delete(deleteImageFromFile);
                context.remove(deleteCategory);
                context.getTransaction().commit();
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }

    private static void AddProduct(){
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try(Session context = sf.openSession()){
            context.beginTransaction();
            Product product = new Product();

            System.out.println("Введіть назву");
            String name = scan.nextLine();
            product.setName(name);

            System.out.println("Виберіть опис");
            String description = scan.nextLine();
            product.setDescription(description);

            System.out.println("Виберіть ціну");
            int price = Integer.valueOf(scan.nextLine());
            product.setPrice(price);
            int i =1;
            String _url = "";
            List<Photo> phts = new ArrayList<>();
            while(i<=7){
                System.out.println("Введіть посилання на фото (URL) ("+i+"/7) e-exit");
                _url = scan.nextLine();
                if(_url.equals("e"))break;
                URL url = new URL(_url);
                BufferedImage image = ImageIO.read(url);
                String photoName = UUID.randomUUID().toString()+".jpg";
                Path path = Paths.get(pathForImagesProducts+photoName);
                ImageIO.write(image,"jpg",path.toFile());
                Photo pht = new Photo();
                pht.setName(photoName);
                pht.setProduct(product);
                phts.add(pht);
                i++;
            }
            product.setPhotos(phts);

            Category category = new Category();
            System.out.println("Введіть id");
            category.setId(Integer.valueOf(scan.nextLine()));
            product.setCategory(category);
            context.save(product);
            context.getTransaction().commit();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    private static void DeleteProduct(){
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try(Session context = sf.openSession()) {
            context.beginTransaction();
            System.out.println("Введіть id");
            int deleteId=Integer.valueOf(scan.nextLine());
            Product deleteProduct = context.createQuery("from Product where id="+deleteId, Product.class).getSingleResult();
            DeletePhotos(deleteId);
            context.remove(deleteProduct);
            context.getTransaction().commit();
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    private static void EditProduct() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try (Session context = sf.openSession()) {
            context.beginTransaction();
            System.out.println("Введіть id");
            int editId = Integer.valueOf(scan.nextLine());
            Product editProduct = context.createQuery("from Product where id="+editId, Product.class).getSingleResult();
            editProduct.setId(editId);
            System.out.println("Введіть нове ім'я");
            String name = scan.nextLine();
            editProduct.setName(name);
            System.out.println("Введіть новий опис");
            String description = scan.nextLine();
            editProduct.setDescription(description);
            DeletePhotos(editId);
            int i =1;
            String _url = "";
            List<Photo> phts = new ArrayList<>();
            while(i<=7){
                System.out.println("Введіть нове посилання на фото (URL) ("+i+"/7) e-exit");
                _url = scan.nextLine();
                if(_url.equals("e"))break;
                URL url = new URL(_url);
                BufferedImage image = ImageIO.read(url);
                String photoName = UUID.randomUUID().toString()+".jpg";
                Path path = Paths.get(pathForImagesProducts+photoName);
                ImageIO.write(image,"jpg",path.toFile());
                Photo pht = new Photo();
                pht.setName(photoName);
                pht.setProduct(editProduct);
                phts.add(pht);
                i++;
            }
            editProduct.setPhotos(phts);

            context.update(editProduct);
            context.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    private static void DeletePhotos(int id){
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try(Session context = sf.openSession()){
            context.beginTransaction();
            List<Photo> phts = context.createQuery("from Photo where product.id="+id, Photo.class).getResultList();

            for (var item:phts){
                Path deleteImageFromFile = Paths.get(pathForImagesProducts+item.getName());
                if(!Files.exists(deleteImageFromFile)) System.out.println("Картинки не існує");
                else Files.delete(deleteImageFromFile);
                context.delete(item);
            }
            context.getTransaction().commit();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }


    private static void ShowProducts(){
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try(Session context = sf.openSession()){
            context.beginTransaction();
            List<Product> list = context.createQuery("from Product ",Product.class).getResultList();
            for(var item : list){
                System.out.println("# "+item.getId()+" | "+item.getName()+" | "+item.getDescription()+" | "+ item.getPrice()+" UAH | "+item.getCategory().getName());
                if(item.getPhotos()!=null){
                    System.out.print("Photos : ");
                for (var pht:item.getPhotos()){
                    System.out.print(pht.getName()+" | ");
                }
                    System.out.println();
                }
            }
            context.getTransaction().commit();
        }
    }
}