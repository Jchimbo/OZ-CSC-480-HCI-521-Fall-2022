package software.design.rest.Resources;

import Admin.Database;
import Admin.User;
import Query.Read;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ws.rs.*;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.DriverManager;

@Path("Discord")
public class DiscordResource {
    Database db = null;
// TODO: Work on MSGBYREACTION
// TODO: Work on REACTIONBYMSG
// TODO: Work on meaningsByEmoji

/*
    In context.doLookup we make a connection based on the context of the resourse in teh server.xml
     the problem with that is that we don't have access to the properties in the datasourse and since all of that is bases on global variables like user and pass that doesn't work
     I can make jndiEntries to like be able to call usernamee and password but the connection to the database would have to still rely on what we did before and that would defeatet
     the purpose of making it run through open liberty so we would have to restructure all classes or we can talk to adam about it and see if we can keep doing it the way we have been/
 */
    /**
     * Returns the Nickname based on the discord_id and server_id response.
     *
     * @param Server_id  the server id
     * @param Discord_id the discord id
     * @return the response
     * @throws Throwable the throwable
     */
    @Path("Nickname/{Server_id}/{Discord_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response Nickname(@PathParam("Server_id") Long Server_id, @PathParam("Discord_id") Long Discord_id) throws Throwable {
        DataSource myDB = InitialContext.doLookup("jdbc/myDB");
//        myDB.getConnection().getMetaData().getUserName();
        try {
//            db = new Admin.Database(Server_id, User.REST);
           db = new Admin.Database(Server_id, myDB);
            myDB.getConnection();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(myDB !=null){

            String nickname = db.read.nickname(Discord_id);
            return Response.status(Response.Status.ACCEPTED).entity(nickname).build();
        }else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    /**
     * Returns the Messages from the server. This those not work at the moment need to talk to DB about it.Maybe add a Server Id in the url always
     *
     * @param Server_id the server id
     * @return the response
     * @throws Throwable the throwable
     */
    @Path("Msg/{Server_id}/{Discord_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response Msg(@PathParam("Server_id") Long Server_id,@PathParam("Discord_id") Long Discord_id) throws Throwable {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        try {
            db = new Admin.Database(Server_id, User.REST);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(db !=null){
            JSONObject jsonObject = db.read.message(Discord_id);
            return Response.status(Response.Status.ACCEPTED).entity(jsonObject.toString()).build();
        }else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
    /**
     * Return the Messages by author response using the Server_id, and the Discoed_id of the author.
     *
     * @param Server_id  the server id
     * @param Discord_id the discord id
     * @return the response
     * @throws Throwable the throwable
     */
    @Path("MsgByAuthor/{Server_id}/{Discord_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response MsgByAuthor(@PathParam("Server_id") Long Server_id, @PathParam("Discord_id") Long Discord_id) throws Throwable {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        try {

            Context env = (Context) new InitialContext().lookup("java:comp/env");
            String restUserInfo = (String) env.lookup("rest");
            System.out.println(restUserInfo);
             db = new Admin.Database(Server_id, User.REST);
       }catch (Exception e){
           e.printStackTrace();
       }
        if(db !=null){
            JSONArray jsonArray = db.read.messagesByAuthor(Discord_id);
            return Response.status(Response.Status.ACCEPTED).entity(jsonArray.toString()).build();
        }else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Path("Msg/{Server_id}/{Discord_id}/{message_id}/{emoji_u}")
    @DELETE
    public void MsgDelete(@PathParam("Server_id") Long Server_id, @PathParam("Discord_id") Long Discord_id,@PathParam("message_id") Long message_id, @PathParam("emoji_u") String emoji) throws Throwable {
        DriverManager.registerDriver(new com.mysql.jdbc.Driver());
        try {
            db = new Admin.Database(Server_id, User.REST);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(db !=null){
            db.delete.reaction(message_id,Discord_id, emoji);
        }
    }
}
