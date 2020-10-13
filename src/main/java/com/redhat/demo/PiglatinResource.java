package com.redhat.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/piglatin/encode")
public class PiglatinResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }

    private PigLatin pigLatin;
    private static final Logger LOG = Logger.getLogger(SlashResource.class);

    @Inject
    @Channel("slack")
    Emitter<PigLatin> slackEmitter;

    public SlashResource() {
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String translate(String input, @PathParam String text) throws UnsupportedEncodingException {
        LOG.info(input);

        HashMap<String, String> paramsMap = new HashMap<String, String>();

        String[] params = input.split("&");
        for (String param : params){
            LOG.info(param.split("=")[0]);
            LOG.info(param.split("=")[1]);
            paramsMap.put( URLDecoder.decode(param.split("=")[0], StandardCharsets.UTF_8.name()) , URLDecoder.decode(param.split("=")[1], StandardCharsets.UTF_8.name()) );
        }
            return piglatinHandler(paramsMap.get("text"));
    }

    private String piglatinHandler(String inputtext){
        pigLatin = new PigLatin(inputtext);
        pigLatin.translateToPigLatin();
        BigInteger fact=BigInteger.valueOf(1); 
        LOG.info(pigLatin.inputText + " translated to " + pigLatin.outputText + " (" + fact + ")");
        slackEmitter.send(pigLatin);
        return pigLatin.outputText;
    }
}