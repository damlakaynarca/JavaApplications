package com.example.javaapplication2;
import java.sql.*;
import java.util.Scanner;

    // Veritabanı Url 'sini ve kimlik bilgilerini tanımla
    public class ContactManager {
        private static final String DB_URL = "jdbc:mysql://localhost:3306/Jss";
        private static final String USER = "root";
        private static final String PASS = "root";

        // Kullanıcı girdisi için scanner nesnesi oluştur
        private static Scanner scanner = new Scanner(System.in);

        // Veritabanına bağlanmak için bir Connection nesnesi oluştur
        private static Connection conn = null;

        // SQL sorgularını çalıştırmak için bir Statement nesnesi oluştur
        private static Statement stmt = null;

        // Sorgu sonuçlarını saklamak için bir ResultSet nesnesi oluştur
        private static ResultSet rs = null;

        // Main method
        public static void main(String[] args) {
            try {
                // Register the JDBC driver
                Class.forName("com.mysql.cj.jdbc.Driver");

                // Veritabanına bağlantı aç
                System.out.println("Connecting to the database...");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);

                //  Bir statement nesnesi oluştur
                stmt = conn.createStatement();

                // Contact tablosunu oluşturmak ve doldurmak için SQL komutlarını çalıştır
                createContactTable();
                insertContact("Damla", "Kaynarca", "kaynarcadamla@gmail.com", "1234567890");
                insertContact("Selin", "Sönmez", "selin.sönmez@hotmail.com", "0987654321");
                insertContact("Turgut", "Dkmen", "dikmen.turgut@hotmail.com", "1357924680");

                //  Soyadı sütununda kısmi bir arama yap
                partialSearch("L");

                // E-posta ile bir kişiyi sil ve silmeyi doğrula
                deleteContact("kaynarcadamla@gmail.com");
                verifyDeletion("selin.sönmez@hotmail.com");

                //Scanner, ResultSet, Statement ve Connection nesnelerini kapat
                scanner.close();
                rs.close();
                stmt.close();
                conn.close();

            } catch (Exception e) {
                // Handle any errors
                e.printStackTrace();
            }
        }

        // Contact tablosunu oluşturmak için bir metod
        public static void createContactTable() throws SQLException {
            // Define the SQL statement to create the table
            String sql = "CREATE TABLE IF NOT EXISTS Contact (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "first_name VARCHAR(255)," +
                    "last_name VARCHAR(255)," +
                    "email VARCHAR(255) UNIQUE," +
                    "phone VARCHAR(255)" +
                    ")";

            // SQL ifadesini çalıştır
            stmt.executeUpdate(sql);
            System.out.println("Created Contact table successfully.");
        }

        //
        public static void insertContact(String firstName, String lastName, String email, String phone) throws SQLException {

            String sql = "INSERT INTO Contact (first_name, last_name, email, phone) VALUES (?, ?, ?, ?)";


            PreparedStatement pstmt = conn.prepareStatement(sql);


            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);


            pstmt.executeUpdate();
            System.out.println("Inserted contact: " + firstName + " " + lastName + ", " + email + ", " + phone);
        }

        //
        public static void partialSearch(String keyword) throws SQLException {
            //
            String sql = "SELECT * FROM Contact WHERE last_name LIKE ?";


            PreparedStatement pstmt = conn.prepareStatement(sql);


            pstmt.setString(1, "%" + keyword + "%");

            rs = pstmt.executeQuery();


            System.out.println("Contacts whose last name contains '" + keyword + "' are:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                System.out.println(id + ", " + firstName + " " + lastName + ", " + email + ", " + phone);
            }
        }

        public static void deleteContact(String email) throws SQLException {
            // Define the SQL statement to delete the contact
            String sql = "DELETE FROM Contact WHERE email = ?";


            PreparedStatement pstmt = conn.prepareStatement(sql);


            pstmt.setString(1, email);


            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Deleted contact with email: " + email + ". Rows affected: " + rowsAffected);
        }


        public static void verifyDeletion(String email) throws SQLException {

            String sql = "SELECT * FROM Contact WHERE email = ?";


            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, email);


            rs = pstmt.executeQuery();


            if (rs.next()) {
                System.out.println("Contact with email: " + email + " still exists.");
            } else {
                System.out.println("Contact with email: " + email + " does not exist.");
            }
        }
    }
    /*İlk olarak, com.mysql.cj.jdbc.Driver sınıfını yüklemek için Class.forName() metodunu var. Bu, JDBC sürücüsünü kaydeder ve veritabanına bağlanmamızı sağlar.

Sonra, DriverManager.getConnection() metodunu kullanarak veritabanına bir bağlantı açarız. Bu metod, veritabanının URL’sini, kullanıcı adını ve şifreyi parametre olarak alır ve bir Connection nesnesi döndürür.

Ardından, conn.createStatement() metodunu kullanarak bir Statement nesnesi oluştururuz. Bu nesne, SQL ifadelerini çalıştırmamızı sağlar.

Daha sonra, createContactTable() metodunu çağırırız. Bu metod, Contact adında bir tablo oluşturmak için bir SQL ifadesi tanımlar ve stmt.executeUpdate() metodunu kullanarak bunu çalıştırır. Tablo zaten varsa, oluşturmaz. Tablonun beş sütunu vardır: id (birincil anahtar ve otomatik artan), first_name, last_name, email (benzersiz) ve phone.

Sonra, insertContact() metodunu üç kez çağırarak Contact tablosuna üç kişi ekleriz. Bu metod, parametre olarak alınan değerleri kullanarak bir SQL ifadesi hazırlar ve bunu PreparedStatement nesnesi ile çalıştırır. PreparedStatement nesnesi, SQL enjeksiyon saldırılarını önlemek ve performansı artırmak için kullanılır. Her bir değer için soru işareti yer tutucusu kullanılır ve pstmt.setString() metodları ile yerine konulur. pstmt.executeUpdate() metodu ile SQL ifadesi çalıştırılır ve tabloya bir satır eklenir.

Daha sonra, partialSearch() metodunu çağırarak soyadında “L” harfi olan kişileri ararız. Bu metod da PreparedStatement nesnesi kullanarak bir SQL ifadesi hazırlar ve çalıştırır. Ancak bu sefer SELECT ifadesini kullanarak tablodan veri alırız. LIKE operatörü ve yüzde işareti ile soyadının herhangi bir yerinde “L” harfi olan kişileri buluruz. pstmt.executeQuery() metodu ile SQL ifadesini çalıştırırız ve bir ResultSet nesnesi alırız. ResultSet nesnesi, sorgu sonucundaki verileri tutar.

Son olarak, deleteContact() metodunu çağırarak e-posta adresine göre bir kişiyi sileriz. Bu metod da PreparedStatement nesnesi kullanarak bir SQL ifadesi hazırlar ve çalıştırır. Ancak bu sefer DELETE ifadesini kullanarak tablodan veri sileriz. WHERE koşulu ile e-posta adresine göre silinecek kişiyi belirtiriz.

Ayrıca verifyDeletion() metodunu çağırarak silme işleminin başarılı olduğunu doğrularız. Bu metod da PreparedStatement nesnesi kullanarak bir SQL ifadesi hazırlar ve çalıştırır. Ancak bu sefer COUNT(*) fonksiyonunu kullanarak tablodaki satır sayısını alırız. Eğer satır sayısı üçten küçükse, silme işleminin başarılı olduğunu söyleriz.

Kodun sonunda scanner.close(), rs.close(), stmt.close() ve conn.close() metotlarını çağırarak kaynakları kapatırız.*/





