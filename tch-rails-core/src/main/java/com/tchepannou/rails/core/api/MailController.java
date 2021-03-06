/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tchepannou.rails.core.api;

import com.tchepannou.util.MimeUtil;
import com.tchepannou.util.StringUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *  <code>MailControllers</code> are the core of email processing.
 *  They are made up of actions (public methods) that are executed for each mail to deliver.
 * </p>
 * <p>
 *  Each mail action render a view located in the <code>/mail/</code> directory,
 *  corresponding to the name of the controller and action after executing code in the action.
 *  For example, the action <code>PostMailController.send()</code> will render the view <code>/mail/post/send.vm</code>.
 *  The email view has the following structure:
 * </p>
 * <ul>
 *  <li>The 1st line is the subject of the email to deliver</li>
 *  <li>The following line is the body of the email to deliver</li>
 * <ul>
 * 
 * @author herve
 */
public class MailController
    extends AbstractController
{
    //-- Static Attributes
    public static final String CLASSNAME_SUFFIX = "MailController";
    private static final Logger LOG = LoggerFactory.getLogger (MailController.class);
    
    //-- Attributes
    private String _template;
    private String __name;
    private MailContext _context;
    private InternetAddress _from;
    private InternetAddress _replyTo;
    private List<String> _to = new ArrayList<String> ();
    private List<String> _cc = new ArrayList<String> ();
    private List<String> _bcc = new ArrayList<String> ();
    private List<File> _attachments = new ArrayList<File> ();
    private String _contentType = MimeUtil.TEXT;
    private String _subject;
    private String _body;
    private String _encoding = "utf-8";
    private Map<String, Object> _viewVariables = new HashMap<String, Object> ();

    //-- Controller override
    public Context getContext ()
    {
        return getMailContext ();
    }


    //-- Public methods
    public void addTo (String to)
    {
        add(_to, to);
    }

    public void addCc (String cc)
    {
        add(_cc, cc);
    }

    public void addBcc (String bcc)
    {
        add(_bcc, bcc);
    }

    private void add(List<String> lst, String email)
    {
        if (!StringUtil.isEmpty (email) && StringUtil.isEmail (email) && !lst.contains (email))
        {
            lst.add(email);
        }
    }

    public void addAttachment (File attachment)
    {
        _attachments.add (attachment);
    }

    public boolean hasAttachments ()
    {
        return !_attachments.isEmpty ();
    }

    public boolean hasRecipients ()
    {
        return !_to.isEmpty () || !_cc.isEmpty () || !_bcc.isEmpty ();
    }

    public I18n getI18n ()
    {
        return I18nThreadLocal.get ();
    }

    /**
     * Add a view variable
     *
     * @param name  Name of the variable
     * @param value Value of the variable
     */
    public void addViewVariable (String name, Object value)
    {
        _viewVariables.put (name, value);
    }

    /**
     * Add a set of view variables
     */
    public void addViewVariables (Map<String, Object> vars)
    {
        if (vars != null)
        {
            _viewVariables.putAll (vars);
        }
    }



    public List<String> getTo ()
    {
        return _to;
    }

    public List<String> getBcc ()
    {
        return _bcc;
    }

    public List<String> getCc ()
    {
        return _cc;
    }

    public String getSubject ()
    {
        return _subject;
    }

    public String getBody ()
    {
        return _body;
    }

    public void setBody (String body)
    {
        _body = body;
    }

    public String getContentType ()
    {
        return _contentType;
    }

    public String getEncoding ()
    {
        return _encoding;
    }

    public MailContext getMailContext ()
    {
        return _context;
    }

    public void setMailContext (MailContext context)
    {
        _context = context;
    }

    public String getFrom ()
    {
        return _from != null ? _from.toString () : null;
    }
    public InternetAddress getFromAddress (){
        return _from;
    }
    
    public void setFrom (String from)
    {
        setFrom(from, null);
    }
    public void setFrom (String from, String displayName)
    {
        try {
            this._from = displayName != null
                ? new InternetAddress(from, displayName)
                : new InternetAddress(from);
        } catch (Exception e){
            LOG.warn ("Invalid address: " + displayName + "<" + from + ">", e);
            this._from = null;
        }        
    }
    
    public String getReplyTo (){
        return _replyTo != null ? _replyTo.toString () : null;
    }
    public InternetAddress getReplyToAddress (){
        return _replyTo;
    }
    public void setReplyTo (String replyTo){
        setReplyTo (replyTo, null);
    }
    public void setReplyTo (String replyTo, String displayName){
        try {
            this._replyTo = displayName != null
                ? new InternetAddress(replyTo, displayName)
                : new InternetAddress(replyTo);
        } catch (Exception e){
            LOG.warn ("Invalid address: " + displayName + "<" + replyTo + ">", e);
            this._from = null;
        }        
        
    }

    public void setSubject (String subject)
    {
        this._subject = subject;
    }

    public void setEncoding (String encoding)
    {
        _encoding = encoding;
    }

    public void setContentType (String contentType)
    {
        _contentType = contentType;
    }

    public Map<String, Object> getViewVariables ()
    {
        return _viewVariables;
    }

    /**
     * Returns the name of the controller
     */
    public String getName ()
    {
        if (__name == null)
        {
            __name = getName (getClass ());
        }
        return __name;
    }

    public List<File> getAttachments ()
    {
        return _attachments;
    }

    /**
     * Returns the computed name of a controller
     */
    public static String getName (Class<? extends MailController> clazz)
    {
        String fqcn = clazz.getName ();
        int i = fqcn.lastIndexOf ('.');
        String name = i>0 ? fqcn.substring (i+1) : fqcn;

        String xname = name.endsWith ("MailController")
            ? name.substring (0, name.length ()-14)
            : name;
        return  xname.substring (0, 1).toLowerCase () + xname.substring (1);
    }

    /**
     * Template to apply to the email.
     * This property is set by the annotation {@link com.tchepannou.rails.core.annotation.WebTemplate}
     */
    public String getTemplate ()
    {
        return _template;
    }

    public void setTemplate (String template)
    {
        this._template = template;
    }
    
    
}
