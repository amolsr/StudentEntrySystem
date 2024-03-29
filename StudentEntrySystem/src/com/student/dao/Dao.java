package com.student.dao;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;

public class Dao {
	private static Connection con;

	public static Connection getConnection() throws SQLException {
		try {
			Class.forName(DBIntializer.DRIVER);
			con = DriverManager.getConnection(DBIntializer.CON_STRING, DBIntializer.USERNAME, DBIntializer.PASSWORD);
		} catch (Exception e) {
			System.out.println(e);
		}
		return con;
	}

	public static int Entry(String studno, String name, String work, Time time) {
		int status = 0;
		try {
			Connection con = Dao.getConnection();
			PreparedStatement ps = con
					.prepareStatement("insert into Entry(studno, name, work, date, entry ) values (?,?,?,?,?)");
			ps.setString(1, studno);
			ps.setString(2, name);
			ps.setString(3, work);
			ps.setDate(4, new java.sql.Date((new Date()).getTime()));
			ps.setString(5, time.toString());
			status = ps.executeUpdate();
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return status;
	}

	public static boolean validate(String studno, String name) {
		boolean status = true;
		try {
			Connection con = Dao.getConnection();
			PreparedStatement ps = con.prepareStatement("select * from Entry where studno = ? and name = ?;");
			ps.setString(1, studno);
			ps.setString(2, name);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String entry = rs.getString("entry");
				String exit = rs.getString("exit");
				if (entry != null && exit == null) {
					status = false;
					break;
				}
			}
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return status;
	}

	public static int Exit(String studno, String name, Time time) {
		int status = 0;
		try {
			Connection con = Dao.getConnection();
			PreparedStatement ps = con
					.prepareStatement("UPDATE Entry SET `exit`=? WHERE studno=? AND name=? AND `exit` IS NULL;");
			ps.setString(1, time.toString());
			ps.setString(2, studno);
			ps.setString(3, name);
			status = ps.executeUpdate();
			con.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return status;
	}

	public static String[][] fetchByDate(Date from, Date to) {
		String[][] arr = null;
		try {
			int i = 0;
			Connection con = Dao.getConnection();
			PreparedStatement ps = con.prepareStatement("SELECT * FROM Entry WHERE date BETWEEN ? AND ?;");
			ps.setDate(1, new java.sql.Date(from.getTime()));
			ps.setDate(2, new java.sql.Date(to.getTime()));
			ResultSet rs = ps.executeQuery();
			int totalRows = 0;
			rs.last();
			totalRows = rs.getRow();
			rs.beforeFirst();
			arr = new String[totalRows][7];
			if (rs.next() == false) {
				return null;
			} else {
				do {
					arr[i][0] = rs.getString("serial");
					arr[i][1] = rs.getString("studno");
					arr[i][2] = rs.getString("name");
					arr[i][3] = rs.getString("work");
					arr[i][4] = (new SimpleDateFormat("dd-MM-yyyy")).format(rs.getDate("date"));
					arr[i][5] = rs.getString("entry");
					arr[i][6] = rs.getString("exit");
					i++;
				} while (rs.next());
			}
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arr;
	}

	public static void getDocument(String header[], String body[][]) throws Exception {
		Document document = new Document();
		Paragraph title = new Paragraph();
		// Create new File
		File file = new File("C:\\Users\\amol8\\Desktop\\Attendence.pdf");
		file.createNewFile();
		FileOutputStream fop = new FileOutputStream(file);
		PdfWriter.getInstance(document, fop);
		document.open();
		// Fonts
		title.add(new Paragraph(" "));
		title.add(new Paragraph("Attendence", new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD)));
		title.add(new Paragraph(" "));
		title.add(new Paragraph("Report generated by: " + System.getProperty("user.name") + ", " + new Date(),
				new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC)));
		title.add(new Paragraph(" "));
		document.add(title);
		Font fontHeader = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
		Font fontBody = new Font(Font.FontFamily.COURIER, 10, Font.NORMAL);
		// Table for header
		PdfPTable table = new PdfPTable(header.length);
		float[] widths = new float[] { 20f, 60f, 70f, 60f, 60f, 50f, 50f };
		table.setWidths(widths);
		for (int j = 0; j < header.length; j++) {
			Phrase frase = new Phrase(header[j], fontHeader);
			PdfPCell cell = new PdfPCell(frase);
			cell.setBackgroundColor(new BaseColor(Color.lightGray.getRGB()));
			table.addCell(cell);
		}
		document.add(table);
		// Table for body
		PdfPTable table1 = new PdfPTable(header.length);
		table1.setWidths(widths);
		for (int i = 0; i < body.length; i++) {
			for (int j = 0; j < body[i].length; j++) {
				table1.addCell(new Phrase(body[i][j], fontBody));
			}
		}
		document.add(table1);
		document.close();
		fop.flush();
		fop.close();
	}
}
