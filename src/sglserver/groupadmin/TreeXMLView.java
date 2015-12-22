package sglserver.groupadmin;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
GUI
*/
class TreeXMLView extends JFrame implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3263514219554862135L;
	private Group_Key_Infomation gki;
	private DefaultMutableTreeNode root;
	//private JTree tree;
	private DefaultTreeModel treeModel;
	
	TreeXMLView(){
		root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);

		JTree tree = new JTree(treeModel);

		// Menuを付ける
		JMenuBar menuBar = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenu menuGroupPeer = new JMenu("Group/Peer");
	
		
		JMenuItem menuItemNew = new JMenuItem("New");
		menuItemNew.addActionListener(this);
		menuItemNew.setActionCommand("New");

		JMenuItem menuItemOpen = new JMenuItem("Open");
		menuItemOpen.addActionListener(this);
		menuItemOpen.setActionCommand("Open");

		JMenuItem menuItemSave = new JMenuItem("Export");
		menuItemSave.addActionListener(this);
		menuItemSave.setActionCommand("Save");

		JMenuItem menuItemSaveAs = new JMenuItem("Save as");
		menuItemSaveAs.addActionListener(this);
		menuItemSaveAs.setActionCommand("SaveAs");

		JMenuItem menuItemExit = new JMenuItem("Exit");
		menuItemExit.addActionListener(this);
		menuItemExit.setActionCommand("Exit");

		JMenuItem menuItemAddGroup = new JMenuItem("Group Add");
		menuItemAddGroup.addActionListener(this);
		menuItemAddGroup.setActionCommand("AddGroup");

		JMenuItem menuItemAddPeer = new JMenuItem("Peer kAdd");
		menuItemAddPeer.addActionListener(this);
		menuItemAddPeer.setActionCommand("AddPeer");

		JMenuItem menuDelete = new JMenuItem("Delete");
		menuDelete.addActionListener(this);
		menuDelete.setActionCommand("Delete");


		menuFile.add(menuItemNew);
		menuFile.add(menuItemOpen);
		menuFile.add(menuItemSave);
		menuFile.add(menuItemSaveAs);
		menuFile.addSeparator();
		menuFile.add(menuItemExit);
		
		menuGroupPeer.add(menuItemAddGroup);
		menuGroupPeer.add(menuItemAddPeer);
		menuGroupPeer.add(menuDelete);
		
		
		
		menuBar.add(menuFile);
		menuBar.add(menuGroupPeer);


		this.setJMenuBar(menuBar);
		
		
		
		JScrollPane scrPane = new JScrollPane();
		scrPane.getViewport().setView(tree);
		scrPane.setPreferredSize(new Dimension(620, 600));
		
		JPanel p1 = new JPanel();
		p1.add(scrPane);
		
		Container content = this.getContentPane();
		content.add(p1, BorderLayout.CENTER);
		
		// ウィンドウが閉じれるように
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		// サイズと位置を指定
		setBounds( 10, 10, 640, 640);
		// 表示
		setVisible(true);
	}
	/*
	 * イベントリスト
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		if (action.equals("New")){
		}
		if (action.equals("Open")){
			OpenXMLDialog();
		}
		if (action.equals("Save")){
			SaveXMLDialog();
		}
		if (action.equals("SaveAs")){
			SaveXMLDialog();
		}
		if (action.equals("Exit")){
			System.exit(0);
		}
	}

	public void OpenXMLDialog(){
		// OpenDialogの準備
		FileDialog openDialog = new FileDialog(this,"Open");
		openDialog.setMode(FileDialog.LOAD);
		String directory = openDialog.getDirectory();
		if(directory != null){openDialog.setDirectory(directory);}
		openDialog.setFile("*.xml");
		openDialog.setVisible(true);
		
		String open_filename = openDialog.getFile();
		if(open_filename != null){
		directory = openDialog.getDirectory();
		//String open_dir_filename = directory + open_filename;

		gki = new Group_Key_Infomation(open_filename);
		
		root.setUserObject("Group_Key_Infomation");
		
		//System.out.println(gki.getGroup(0));
		
		root = getTreeNode(root,gki.getGroup(0));
			
		treeModel.reload();
		}
		

	}
	public void SaveXMLDialog(){
		String save_filename;
		
		FileDialog saveDialog = new FileDialog(this,"Save");
		saveDialog.setMode(FileDialog.SAVE);
		String directory = saveDialog.getDirectory();
		if(directory != null){saveDialog.setDirectory(directory);}
		saveDialog.setVisible(true);
		if(directory != null){
		save_filename = directory + saveDialog.getFile();}
		else{
		save_filename = saveDialog.getFile();}
		
		SaveXMLDocument(save_filename);
		
		
	}
/*
 * グループリストを再帰的にTreeに変換する関数
 */
	public	DefaultMutableTreeNode getTreeNode(DefaultMutableTreeNode dmtn, Group g){
		DefaultMutableTreeNode group = new DefaultMutableTreeNode( "GroupName:"+ g.getGroupName());

		for(int j=0; j<g.getListSize(); j++){
			
				User user = new User();
				user = g.getUser(j);
			
				if (user.getParentGroup() == g){
				
					DefaultMutableTreeNode id = new DefaultMutableTreeNode( "ID:"+ user.getID() );
					DefaultMutableTreeNode name = new DefaultMutableTreeNode( "Name:"+ user.getName() );
					DefaultMutableTreeNode ip = new DefaultMutableTreeNode( "IP:"+ user.getIP() );
					DefaultMutableTreeNode rip = new DefaultMutableTreeNode( "RelayIP:"+ user.getRelayIP() );

					id.add(name);
					id.add(ip);
					id.add(rip);
			
					group.add(id);
				}
			}
		dmtn.add(group);
		for(int i=0; i<g.getGroupListSize(); i++){
			dmtn.add(getTreeNode(group,g.getGroup(i)));
		}
		
	return dmtn;
	}
	//Groupとユーザーのリストから、XML Documentを返す
	public void SaveXMLDocument(String FileName){
		// ドキュメントビルダーファクトリを生成
		try{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = dbf.newDocumentBuilder();

		Document xmldoc = builder.newDocument();
/*
 * ここから保存部分。
 */
		
		Element GKI = (Element)xmldoc.createElement("Group_Key_Information");
		xmldoc.appendChild(GKI);

		Element gc = xmldoc.createElement("GroupCount");
		gc.appendChild(xmldoc.createTextNode(String.valueOf(gki.getGroupCount())));
		GKI.appendChild( gc );
		
		//Rootから再帰的に追加。
		//Element gpr = 
			getTreeDocument(xmldoc,GKI,gki.getGroup(0));
		
		gki.saveFile(xmldoc , FileName);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		}

/*
 * グループリストを再帰的にXMLに変換する関数
 */
	public	Element getTreeDocument(Document document,Element element ,Group g){
		
		Attr attr=document.createAttribute("xmlns:GroupName");
		attr.setValue(g.getGroupName());
		
		Element gp = document.createElement("Group");
		gp.setAttributeNode(attr);
		
		for(int j=0; j<g.getListSize(); j++){
			
				User user = new User();
				user = g.getUser(j);
			
				if (user.getParentGroup() == g){
				
					Element uelement = document.createElement("Peer");
					
					Element id = document.createElement("ID");
					id.appendChild(document.createTextNode(String.valueOf(user.getID())));
					uelement.appendChild(id);
					
					Element name = document.createElement("Name");
					name.appendChild(document.createTextNode(String.valueOf(user.getName())));
					uelement.appendChild(name);
					
					Element ip = document.createElement("IP");
					ip.appendChild(document.createTextNode(String.valueOf(user.getIP())));
					uelement.appendChild(ip);
					
					Element rip = document.createElement("RelayIP");
					rip.appendChild(document.createTextNode(String.valueOf(user.getRelayIP())));
					uelement.appendChild(rip);
					
					gp.appendChild(uelement);
				}
			}
		element.appendChild(gp);

		for(int i=0; i<g.getGroupListSize(); i++){
			element.appendChild(getTreeDocument(document,gp,g.getGroup(i)));
		}
		
	return element;
	}

}