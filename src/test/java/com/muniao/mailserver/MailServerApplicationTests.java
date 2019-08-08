package com.muniao.mailserver;

import com.muniao.mailserver.domain.User;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServerApplicationTests
{
    @Autowired
    JavaMailSender javaMailSender;

    @Test
    public void sendSimpleMail()
    {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setSubject("这是一封简单的测试邮件");
        smm.setFrom("muniaotec@163.com");
        smm.setTo("lixiaopengsz@163.com");
        smm.setCc("846358830@qq.com");
        smm.setSentDate(new Date());
        smm.setText("邮件正文：你好啊，我是牛逼的嗷嗷叫的");

        javaMailSender.send(smm);
    }

    /**
     * 发送带附件的邮件
     * @throws MessagingException
     */
    @Test
    public void sendAttachFileMail() throws MessagingException
    {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
        helper.setSubject("这是一封测试邮件");
        helper.setFrom("muniaotec@163.com");
        helper.setTo("lixiaopengsz@163.com");
        helper.setCc("846358830@qq.com");
        //helper.setBcc("14xxxxx098@qq.com");
        helper.setSentDate(new Date());
        helper.setText("这是测试邮件的正文");
        helper.addAttachment("baogao.jpg",new File("C:\\1.jpg"));
        javaMailSender.send(mimeMessage);
    }

    /**
     * 发送带图片资源的邮件
     * @throws MessagingException
     */
    @Test
    public void sendImgResMail() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("这是一封测试邮件");
        helper.setFrom("muniaotec@163.com");
        helper.setTo("lixiaopengsz@163.com");
        helper.setCc("846358830@qq.com");
        //helper.setBcc("14xxxxx098@qq.com");
        helper.setSentDate(new Date());
        helper.setText("<p>hello 大家好，这是一封测试邮件，这封邮件包含两种图片，分别如下</p><p>第一张图片：</p><img src='cid:p01'/><p>第二张图片：</p><img src='cid:p02'/>",true);
        helper.addInline("p01",new FileSystemResource(new File("C:\\1.jpg")));
        helper.addInline("p02",new FileSystemResource(new File("C:\\1.jpg")));
        javaMailSender.send(mimeMessage);
    }

    @Test
    public void sendFreemarkerMail() throws MessagingException, IOException, TemplateException
    {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("这是 sendFreemarkerMail 一封测试邮件");
        helper.setFrom("muniaotec@163.com");
        helper.setTo("lixiaopengsz@163.com");
        helper.setCc("846358830@qq.com");
        //helper.setBcc("14xxxxx098@qq.com");
        helper.setSentDate(new Date());
        //构建 Freemarker 的基本配置
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_0);
        // 配置模板位置
        ClassLoader loader = MailServerApplication.class.getClassLoader();
        configuration.setClassLoaderForTemplateLoading(loader, "templates");
        //加载模板
        Template template = configuration.getTemplate("mail.ftl");
        User user = new User();
        user.setUsername("javaboy");
        user.setNum(1);
        user.setSalary((double) 99999);
        StringWriter out = new StringWriter();
        //模板渲染，渲染的结果将被保存到 out 中 ，将out 中的 html 字符串发送即可
        template.process(user, out);
        helper.setText(out.toString(),true);
        javaMailSender.send(mimeMessage);
    }

    @Autowired
    TemplateEngine templateEngine;

    @Test
    public void sendThymeleafMail() throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("这是sendThymeleafMail一封测试邮件");
        helper.setFrom("muniaotec@163.com");
        helper.setTo("lixiaopengsz@163.com");
        helper.setCc("846358830@qq.com");
        //helper.setBcc("14xxxxx098@qq.com");
        helper.setSentDate(new Date());
        Context context = new Context();
        context.setVariable("username", "javaboy");
        context.setVariable("num","000001");
        context.setVariable("salary", "99999");
        String process = templateEngine.process("mail.html", context);
        helper.setText(process,true);
        javaMailSender.send(mimeMessage);
    }


}
