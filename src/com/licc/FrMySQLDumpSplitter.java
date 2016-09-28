package com.licc;

import java.awt.event.*;
import java.io.*;

import javax.swing.*;

public class FrMySQLDumpSplitter extends JFrame {

	private static final long serialVersionUID = 1L;

	private JButton btnExit;
	private JButton btnGenerateFiles;
	private JFileChooser sourceFileChooser;
	private JTextField jTextField1;
	private JTextField tfDumpfile;
	
	String previousTableName = "";
	String currentTableName = "";

	String myDumpfileDirectory = "";

	public FrMySQLDumpSplitter() {
		previousTableName = "";
		currentTableName = "";

		initComponents();

	}

	private void initComponents() {

		sourceFileChooser = new JFileChooser();
		btnGenerateFiles = new JButton();
		tfDumpfile = new JTextField();
		btnExit = new JButton();
		jTextField1 = new JTextField();

		sourceFileChooser.setDialogTitle("Dumpfile");

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("MySQL Dump file splitter");
		
		btnGenerateFiles.setText("Generate file(s)");
		btnGenerateFiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnGenerateFilesActionPerformed(evt);
			}
		});

		tfDumpfile.setText("Choose dumpfile...");
		tfDumpfile.setToolTipText("Paste filename in here or choose file");
		tfDumpfile.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				OnClickTFDumpfile(evt);
			}
		});
		tfDumpfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				tfDumpfileActionPerformed(evt);
			}
		});

		btnExit.setText("Exit");
		btnExit.setToolTipText("Exit the application");
		btnExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				btnExitActionPerformed(evt);
			}
		});

		jTextField1.setText("Destination file (cannot be the same as source).");
		jTextField1.setToolTipText("Destination (to be implemented...");
		jTextField1.setEnabled(false);

		GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(143, Short.MAX_VALUE).addGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE).addContainerGap())
				.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addGroup(layout.createSequentialGroup().addComponent(btnGenerateFiles).addGap(37, 37, 37).addComponent(btnExit)).addGroup(layout.createSequentialGroup()
								.addComponent(tfDumpfile, GroupLayout.PREFERRED_SIZE, 473, GroupLayout.PREFERRED_SIZE).addGap(18, 18, 18)))).addGap(128, 128, 128)))));
		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGap(140, 140, 140)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false).addComponent(tfDumpfile)).addGap(18, 18, 18)
				.addGap(21, 21, 21).addGap(31, 31, 31).addComponent(jTextField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 52, Short.MAX_VALUE).addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(btnGenerateFiles).addComponent(btnExit)).addGap(72, 72, 72)));

		pack();
		setLocationRelativeTo(null);
	}

	private void jButton2ActionPerformed(ActionEvent evt) {
		int returnVal = sourceFileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = sourceFileChooser.getSelectedFile();
			tfDumpfile.setText(file.getAbsolutePath());

			myDumpfileDirectory = file.getParent();

			myDumpfileDirectory = myDumpfileDirectory + File.separator;

			JOptionPane.showMessageDialog(null, "You have chosen the dir " + myDumpfileDirectory, "Should be OK!", JOptionPane.WARNING_MESSAGE);
		} else {
			System.out.println("File access cancelled by user.");
		}
	}

	private void btnExitActionPerformed(ActionEvent evt) {
		System.exit(0);
	}

	private void btnGenerateFilesActionPerformed(ActionEvent evt) {

		if (tfDumpfile.getText().equals("Choose dumpfile...")) {
			JOptionPane.showMessageDialog(null, "You must choose a dumpfile!", "Warning! Error!", JOptionPane.WARNING_MESSAGE);
			return;
		}

		try {

			JOptionPane.showMessageDialog(null, "You must wait for the processing to finish (another message will appear!", "Warning!", JOptionPane.WARNING_MESSAGE);

			BufferedReader myDumpfileReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(tfDumpfile.getText())),"UTF-8"));

			SQLFormatHelper sqlHelper = new SQLFormatHelper();

			String dumpFileLine = myDumpfileReader.readLine();

			outer: while (dumpFileLine != null) {
				if (dumpFileLine.startsWith("-- Table structure")) {
					String[] parts = dumpFileLine.split(" ");
					currentTableName = parts[4];
					System.out.println(" extract " + currentTableName + "......");
					BufferedWriter myTableFileWriter = new BufferedWriter(new FileWriter(myDumpfileDirectory + sqlHelper.RemoveMySQLQuotes(currentTableName) + ".sql"));

					myTableFileWriter.write("-- Table structure for table " + currentTableName + "\n");
					
					while ((dumpFileLine = myDumpfileReader.readLine()) != null) {
						if(dumpFileLine.startsWith("-- Table structure")) {
							myTableFileWriter.flush();
							myTableFileWriter.close();
							continue outer;
						}
						myTableFileWriter.write(dumpFileLine + "\n");
					}
					myTableFileWriter.flush();
					myTableFileWriter.close();
					System.out.println(currentTableName + " extract complete!");
				}
				
				dumpFileLine = myDumpfileReader.readLine();
			}
			myDumpfileReader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		System.out.println("Processing is complete");
		JOptionPane.showMessageDialog(null, "Processing is complete", "Message", JOptionPane.INFORMATION_MESSAGE);
	}

	private void tfDumpfileActionPerformed(ActionEvent evt) {
		
	}

	private void OnClickTFDumpfile(MouseEvent evt) {
		this.jButton2ActionPerformed(null);
	}

	public static void main(String args[]) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(FrMySQLDumpSplitter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(FrMySQLDumpSplitter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(FrMySQLDumpSplitter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(FrMySQLDumpSplitter.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
	}

}
