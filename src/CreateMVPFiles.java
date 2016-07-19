import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xuelang1 on 16/7/1.
 */
public class CreateMVPFiles extends AnAction {
    private Project project;
    private JDialog jFrame;
    JTextField name;
    JTextField username;
    JRadioButton activityJB;
    JRadioButton fragmentJB;
    /*包名*/
    private String packagebase="";


    private enum  CodeType {
        Action,Activity,Fragment,CallBack,Controller,DataAction,DataCallBack,DataSupport
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        project = event.getData(PlatformDataKeys.PROJECT);
        packagebase = readPackageName();
        initSelectView();
        project.getProjectFilePath();
    }

    private void initSelectView() {
        jFrame = new JDialog();// 定义一个窗体Container container = getContentPane();
        jFrame.setModal(true);
        Container container = jFrame.getContentPane();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        JPanel panelname = new JPanel();// /定义一个面板
        panelname.setLayout(new GridLayout(1, 2));
        panelname.setBorder(BorderFactory.createTitledBorder("命名"));

        name = new JTextField();
        name.setText("请输入组件名字");
        panelname.add(name);

        username = new JTextField();
        username.setText("请输入注释的作者");
        panelname.add(username);

        container.add(panelname);


        activityJB = new JRadioButton("Activity");// 定义一个单选按钮
        fragmentJB = new JRadioButton("Fragment");// 定义一个单选按钮

        activityJB.setSelected(true);

        JPanel panel = new JPanel();// /定义一个面板

        panel.setBorder(BorderFactory.createTitledBorder("选择生成代码的类型"));// 定义一个面板的边框显示条
        panel.setLayout(new GridLayout(1, 2));// 定义排版，一行三列
        panel.add(activityJB);// 加入组件
        panel.add(fragmentJB);// 加入组件

        ButtonGroup group = new ButtonGroup();
        group.add(activityJB);
        group.add(fragmentJB);
        container.add(panel);// 加入面板

        JPanel menu = new JPanel();
        menu.setLayout(new FlowLayout());

        Button cancle = new Button();
        cancle.setLabel("取消");
        cancle.addActionListener(actionListener);

        Button ok = new Button();
        ok.setLabel("确定");
        ok.addActionListener(actionListener);
        menu.add(cancle);
        menu.add(ok);
        container.add(menu);


        jFrame.setSize(400, 200);
        jFrame.setLocationRelativeTo(null);

        jFrame.setVisible(true);
    }

    private String readPackageName() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(project.getBasePath() + "/App/src/main/AndroidManifest.xml");

            NodeList dogList = doc.getElementsByTagName("manifest");
            for (int i = 0; i < dogList.getLength(); i++) {
                Node dog = dogList.item(i);
                Element elem = (Element) dog;
                return elem.getAttribute("package");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("取消")) {
                jFrame.dispose();
            } else {
                jFrame.dispose();
                clickCreateFile();
                Messages.showInfoMessage(project,"生成完毕","提示");

            }

        }
    };


    private void clickCreateFile(){
        createFiles(CodeType.Action);
        if(activityJB.isSelected()){
            createFiles(CodeType.Activity);
        }
        if(fragmentJB.isSelected()){
            createFiles(CodeType.Fragment);
        }
        createFiles(CodeType.Controller);
        createFiles(CodeType.DataAction);
        createFiles(CodeType.DataCallBack);
        createFiles(CodeType.DataSupport);
        createFiles(CodeType.CallBack);
    }


    /**
     * 创建文件
     */
    private void createFiles(CodeType codeType) {
        String filename="";
        String content = "";

        String packagepath = packagebase.replace(".","/");
        String presenter = "presenter";
        String apppath = project.getBasePath()+"/App/src/main/java/"+packagepath+"/";
        switch (codeType){
            case Action:
                filename = "TemplateAction.txt";
                content = ReadFile(filename);
                // 1.通用流程,处理顶部注释
                content  = dealFileTitle(content);
                content = dealAction(content);
                writetoFile(content, apppath+presenter+"/viewaction", name.getText() + "Action.java");
                break;
            case Activity:
                filename = "TemplateActivity.txt";
                content = ReadFile(filename);
                // 1.通用流程,处理顶部注释
                content  = dealFileTitle(content);
                //处理activity
                content = dealActivity(content);
                writetoFile(content, apppath+"view/activity", name.getText() + "Activity.java");
                break;
            case CallBack:
                filename = "TemplateCallBack.txt";
                content = ReadFile(filename);
                // 1.通用流程,处理顶部注释
                content  = dealFileTitle(content);

                content = dealCallBack(content);
                writetoFile(content, apppath+presenter+"/viewcallback", name.getText() + "CallBack.java");
                break;
            case Controller:
                filename = "TemplateController.txt";
                content = ReadFile(filename);
                // 1.通用流程,处理顶部注释
                content  = dealFileTitle(content);

                content = dealController(content);
                writetoFile(content, apppath+presenter+"/controller", name.getText() + "Controller.java");
                break;
            case DataAction:
                filename = "TemplateDataAction.txt";
                content = ReadFile(filename);
                // 1.通用流程,处理顶部注释
                content  = dealFileTitle(content);
                //
                content = dealDataAction(content);
                writetoFile(content, apppath+"model/dataaction", name.getText() + "DataAction.java");

                break;
            case DataCallBack:
                filename = "TemplateDataCallBack.txt";
                content = ReadFile(filename);
                // 1.通用流程,处理顶部注释
                content  = dealFileTitle(content);
                // 2
                content = dealDataCallBack(content);
                writetoFile(content, apppath+"model/datacallback", name.getText() + "DataCallBack.java");
                break;
            case DataSupport:
                filename = "TemplateDataSupport.txt";
                content = ReadFile(filename);
                // 1.通用流程,处理顶部注释
                content  = dealFileTitle(content);
                // 2
                content = dealDataSupport(content);
                writetoFile(content, apppath+"model/datasupport", name.getText() + "DataSupport.java");
                break;
            case Fragment:
                filename = "TemplateFragment.txt";
                content = ReadFile(filename);
                // 1.通用流程,处理顶部注释
                content  = dealFileTitle(content);
                //处理fragment
                content = dealFragment(content);
                writetoFile(content, apppath+"view/fragment", name.getText() + "Fragment.java");
                break;
        }

    }

    private String ReadFile(String filename){
        InputStream in = null;
        in = this.getClass().getResourceAsStream("/Template/"+filename);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (Exception e) {
        }
        return content;
    }

    /**
     * 处理activity
     * @param content
     * @return
     */
    private String dealActivity(String content){
        content = content.replace("$name",name.getText());
        content = content.replace("$packagename", packagebase+".view.activity");
        return content;
    }

    /**
     * 处理fragment
     * @param content
     * @return
     */
    private String dealFragment(String content){
        content = content.replace("$name",name.getText());
        content = content.replace("$packagename", packagebase+".view.fragment");
        return content;
    }

    /**
     * 处理action
     * @param content
     * @return
     */
    private String dealAction(String content){
        content = content.replace("$name",name.getText());
        content = content.replace("$packagename", packagebase+".presenter.viewaction");
        return content;
    }

    /**
     * 处理callback
     * @return
     */
    private String dealCallBack(String content){
        content = content.replace("$name",name.getText());
        content = content.replace("$packagename", packagebase+".presenter.viewcallback");
        return content;
    }

    /**
     * 处理controller
     * @param content
     * @return
     */
    private String dealController(String content){
        content = content.replace("$name",name.getText());
        content = content.replace("$packagename", packagebase+".presenter.controller");
        return content;
    }

    /**
     * 处理dataaction
     * @param content
     * @return
     */
    private String dealDataAction(String content){
        content = content.replace("$name",name.getText());
        content = content.replace("$packagename", packagebase+".model.dataaction");
        return content;
    }

    /**
     * 处理datacallback
     * @param content
     * @return
     */
    private String dealDataCallBack(String content){
        content = content.replace("$name",name.getText());
        content = content.replace("$packagename", packagebase+".model.datacallback");
        return content;
    }

    /**
     * 处理datasupport
     * @param content
     * @return
     */
    private String dealDataSupport(String content){
        content = content.replace("$name",name.getText());
        content = content.replace("$packagename", packagebase+".model.datasupport");
        return content;
    }
    /**
     * 处理
     *
     * @param content
     */
    private String dealFileTitle(String content) {
        content = content.replace("$author", username.getText());
        content = content.replace("$packagebase", packagebase);

        content = content.replace("$date", getNowDateShort());
        return content;
    }
    public String getNowDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
    private void writetoFile(String content, String filepath, String filename) {
        try {
            File floder = new File(filepath);
            // if file doesnt exists, then create it
            if (!floder.exists()) {
                floder.mkdirs();
            }
            File file = new File(filepath + "/" + filename);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
                System.out.println(new String(buffer));
            }

        } catch (IOException e) {
        } finally {
            outSteam.close();
            inStream.close();
        }
        return outSteam.toByteArray();
    }

}