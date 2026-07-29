package ec.edu.monster.persistencia;
import java.sql.*;
public final class ConexionBD { private ConexionBD(){} static{try{Class.forName("com.mysql.cj.jdbc.Driver");}catch(ClassNotFoundException e){throw new ExceptionInInitializerError(e);}} public static Connection conectar()throws SQLException{return DriverManager.getConnection("jdbc:mysql://3.239.254.34:3306/eurekarestjava?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC","admin","SqlAmazon2026!");} }
