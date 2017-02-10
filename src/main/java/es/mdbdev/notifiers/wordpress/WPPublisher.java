package es.mdbdev.notifiers.wordpress;

import net.bican.wordpress.Post;
import net.bican.wordpress.Term;
import net.bican.wordpress.Wordpress;
import net.bican.wordpress.exceptions.InsufficientRightsException;
import net.bican.wordpress.exceptions.InvalidArgumentsException;
import net.bican.wordpress.exceptions.ObjectNotFoundException;
import redstone.xmlrpc.XmlRpcFault;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mario de Benito on 08/02/2017.
 *
 * Class that allows the creation of a new post on a Wordpress blog
 */
public class WPPublisher{
    private Wordpress wp;

    /**
     * Default constructor
     * @param username Wordpress username with privileges to create new posts
     * @param password Wordpress user password
     * @param xmlrpc URL of the xmlrpc.php file on the Wordpress installation
     */
    public WPPublisher(String username, String password, String xmlrpc){
        try {
            this.wp = new Wordpress(username, password, xmlrpc);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Publishes a new post
     * @param text Contents of the post (can be HTML)
     * @param category Taxonomy category for the post
     * @param title Title of the post
     * @param status Status of the post (recommended: draft)
     * @throws Exception
     */
    public void doNotify(String text,String category,String title, String status) throws Exception {

        if(wp != null){
            try {
                Integer termId = null;

                List<Term> termsToAdd = new ArrayList<Term>();

                List<Term> terms = wp.getTerms("category");
                for(Term t : terms){
                    if(t.getName().equals(category)){
                        termsToAdd.add(t);
                    }
                }


                Post newPost = new Post();
                newPost.setPost_title(title);
                newPost.setPost_content(text);
                newPost.setPost_status(status);
                newPost.setTerms(termsToAdd);


                Integer result = wp.newPost(newPost);
            } catch (InsufficientRightsException e) {
                e.printStackTrace();
                throw new IOException("WP: Insufficient Rights");
            } catch (InvalidArgumentsException e) {
                e.printStackTrace();
                throw new IOException("WP: Invalid arguments");
            } catch (ObjectNotFoundException e) {
                e.printStackTrace();
                throw new IOException("WP: Object not found");
            } catch (XmlRpcFault xmlRpcFault) {
                xmlRpcFault.printStackTrace();
                throw new IOException("WP: XML RPC Fault");
            }
        }else{
            throw new Exception("WP: Could not create a Wordpress API connection.");
        }

    }




}
