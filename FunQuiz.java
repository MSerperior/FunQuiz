/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package funquiz;

/**
 *
 * @author dany
 */
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.Image;
import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.border.Border;

public class FunQuiz extends JFrame implements ActionListener{
    
    private static String role, mapelStr, query;
    
    private static Connection con;
    //main panel
    private final CardLayout cl = new CardLayout();
    private final JPanel p = new JPanel(cl);
    
    /*
        selection guru dan siswa
    */
    JPanel opening = new JPanel();
    JButton guruBtn = new JButton("Guru");
    JButton siswaBtn = new JButton("Siswa");
    
    /*
        login page
    */
    JPanel loginPage = new JPanel();
    JLabel usernameLb = new JLabel("username : ");
    JLabel passwordLb = new JLabel("password : ");
    JTextField usernameTf = new JTextField(15);
    JTextField passwordTf = new JTextField(15);
    JButton loginBtn = new JButton("Login");
    
    /*
        pilihan mata pelajaran
    */
    JPanel mapel = new JPanel(new GridLayout(4,2));
    JButton[] mapelBtn = new JButton[10];
    ButtonGroup mapelBGroup = new ButtonGroup();
    ActionListener[] AL = new ActionListener[10];
    /* 
        menu guru
    */
    JPanel selectionGuru = new JPanel();
    JButton tmbhSoalBtn = new JButton("Tambah Soal");
    JButton lihatNilaiBtn = new JButton("Lihat Nilai Siswa");
    
    /*
        Halaman Soal
    */
    
    
    static JPanel halamanSoal = new JPanel(new BorderLayout());
    static JRadioButton[][] RButton;
    
    //center
    static JPanel centerSoal = new JPanel();
    static JScrollPane scroll_pane = new JScrollPane(
            centerSoal,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
    );
    
    //east
    static JPanel eastSoal = new JPanel();
    static JButton submitBtn = new JButton("Submit");
    
    
    
    
    public FunQuiz() throws SQLException{
        super("Fun Quiz");
//        super(icon);
        setResizable(false);
        setExtendedState(getExtendedState()|JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        
        
        mapelBtn[0] = new JButton("Matematika");
        mapelBtn[2] = new JButton("Fisika");
        mapelBtn[4] = new JButton("Kimia");
        mapelBtn[6] = new JButton("Biologi");
        mapelBtn[1] = new JButton("Sejarah");
        mapelBtn[3] = new JButton("Geografi");
        mapelBtn[5] = new JButton("Ekonomi");
        mapelBtn[7] = new JButton("Sosiologi");
        
        /*
            setup opening
        */
        opening.add(guruBtn);
        opening.add(siswaBtn);
        guruBtn.addActionListener(new ActionListener(){
           @Override
           public void actionPerformed(ActionEvent e){
               role = guruBtn.getText();
               cl.show(p, "loginPage");
           } 
        });
        siswaBtn.addActionListener(new ActionListener(){
           @Override
           public void actionPerformed(ActionEvent e){
               role = siswaBtn.getText();
               cl.show(p, "loginPage");
           } 
        });
        /*
            setup login
        */
        loginPage.add(usernameLb);
        loginPage.add(usernameTf);
        loginPage.add(passwordLb);
        loginPage.add(passwordTf);
        loginPage.add(loginBtn);
        
        loginBtn.addActionListener(new ActionListener(){
           @Override
           public void actionPerformed(ActionEvent e){
               cl.show(p, "mapel");
           } 
        });
        
        /*
            setup mapel
        */
        
        for(int i = 0; i < 8; i++) {
            mapel.add(mapelBtn[i]);
        }
        
        mapelBtn[0].addActionListener((ActionEvent e) -> {
            mapelStr = mapelBtn[0].getText(); 
            query = "SELECT firstName, lastName, email FROM Customer;";
            try {
                generateSoal();
            } catch (SQLException ex) {
                Logger.getLogger(FunQuiz.class.getName()).log(Level.SEVERE, null, ex);
            }
            cl.show(p, "halamanSoal");
        });
        mapelBtn[1].addActionListener((ActionEvent e) -> {
            mapelStr = mapelBtn[1].getText(); 
            query = "SELECT * FROM "+ mapelStr +";";
        });
        mapelBtn[2].addActionListener((ActionEvent e) -> {
            mapelStr = mapelBtn[2].getText(); 
            query = "SELECT * FROM "+ mapelStr +";";
        });
        mapelBtn[3].addActionListener((ActionEvent e) -> {
            mapelStr = mapelBtn[3].getText(); 
            query = "SELECT * FROM "+ mapelStr +";";
        });
        mapelBtn[4].addActionListener((ActionEvent e) -> {
            mapelStr = mapelBtn[4].getText(); 
            query = "SELECT * FROM "+ mapelStr +";";
        });
        mapelBtn[5].addActionListener((ActionEvent e) -> {
            mapelStr = mapelBtn[5].getText(); 
            query = "SELECT * FROM "+ mapelStr +";";
        });
        mapelBtn[6].addActionListener((ActionEvent e) -> {
            mapelStr = mapelBtn[6].getText(); 
            query = "SELECT * FROM "+ mapelStr +";";
        });
        mapelBtn[7].addActionListener((ActionEvent e) -> {
            mapelStr = mapelBtn[7].getText(); 
            query = "SELECT * FROM "+ mapelStr +";";
        });
       
        
        /*
            setup menu guru
        */
        selectionGuru.add(tmbhSoalBtn);
        selectionGuru.add(lihatNilaiBtn);
        
        p.add(opening, "opening");
        p.add(loginPage, "loginPage");
        p.add(mapel, "mapel");
        p.add(selectionGuru, "menu guru");
        p.add(halamanSoal, "halamanSoal");
        add(p);
        cl.show(p, "opening");
        setVisible(true);
    }
    
    public static void generateSoal() throws SQLException{
        JLabel Jarr[] = new JLabel[10];
        JPanel AnsPanel[] = new JPanel[10];
        ButtonGroup[] bgroup = new ButtonGroup[10];
        RButton = new JRadioButton[10][2];
        for(int i = 0; i < 10; i++){
            bgroup[i] = new ButtonGroup();
        }
        ResultSet rs;
        Statement stmt = con.createStatement();
        rs = stmt.executeQuery(query);
        int ptr = 0;
        while(rs.next()){
            int numColumns = rs.getMetaData().getColumnCount();
            Jarr[ptr] = new JLabel((ptr+1) + ". " + (String) rs.getObject(1));
            RButton[ptr][0] = new JRadioButton((String) rs.getObject(2));
            RButton[ptr][1] = new JRadioButton((String) rs.getObject(3));

            bgroup[ptr].add(RButton[ptr][0]);
            bgroup[ptr].add(RButton[ptr][1]);
            
            AnsPanel[ptr] = new JPanel();
            AnsPanel[ptr].setLayout(new GridLayout(2,1));
            AnsPanel[ptr].add(RButton[ptr][0]);
            AnsPanel[ptr].add(RButton[ptr][1]);
            
            ptr++;
        }
        
        for(int i = 0; i < ptr; i++){
            centerSoal.add(Jarr[i]);
            centerSoal.add(AnsPanel[i]);
        }
        /*
            Setup halaman soal
        */
        
        //Center
        centerSoal.setLayout(new GridLayout(50,1));
        halamanSoal.add(scroll_pane, BorderLayout.CENTER);
        //east
        eastSoal.setLayout(new GridLayout(10,1));
        eastSoal.add(submitBtn);
        halamanSoal.add(eastSoal,BorderLayout.EAST);
        
    }
    
    static void createConnection() throws SQLException{
          try {
            Class.forName("com.mysql.jdbc.Driver");
            
          } catch (ClassNotFoundException e) {
            Logger.getLogger(FunQuiz.class.getName()).log(Level.SEVERE, null, e);
        }
          try {
            FunQuiz.con = DriverManager.getConnection("jdbc:mysql://localhost/test?useSSL=true", "root", "root");
            System.out.println("Connection Success");
          } catch (Exception e) {
              System.out.println(e);
          }
          
    }
    public static void main(String[] args) throws SQLException{
        createConnection();
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost/test?useSSL=true", "root", "root");
        new FunQuiz();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
