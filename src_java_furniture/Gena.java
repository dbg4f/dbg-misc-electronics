import java.io.FileWriter;
import java.io.IOException;

public class Gena {

  public static final String TEMPLATE = "<f1:contact tempid=\"1\">\r\n" +
          "  <f1:firstname>Contact%I</f1:firstname>\r\n" +
          "  <f1:lastname>Public%I</f1:lastname>\r\n" +
          " \r\n" +
          "  <f1:tel type=\"home\" indx=\"1\">\r\n" +
          "    <f1:number>0000%I</f1:number>\r\n" +
          "   <f1:preference>0</f1:preference>\r\n" +
          "   </f1:tel>\r\n" +
          "     <f1:tel type=\"businessfax\" indx=\"1\">\r\n" +
          "    <f1:number>0000%I</f1:number>\r\n" +
          "   <f1:preference>0</f1:preference>\r\n" +
          "   </f1:tel>\r\n" +
          "     <f1:tel type=\"business\" indx=\"1\">\r\n" +
          "    <f1:number>0000%I</f1:number>\r\n" +
          "   <f1:preference>0</f1:preference>\r\n" +
          "   </f1:tel>\r\n" +
          "     <f1:tel type=\"homefax\" indx=\"1\">\r\n" +
          "    <f1:number>0000%I</f1:number>\r\n" +
          "   <f1:preference>0</f1:preference>\r\n" +
          "   </f1:tel>\r\n" +
          "     <f1:tel type=\"mobile\" indx=\"1\">\r\n" +
          "    <f1:number>0000%I</f1:number>\r\n" +
          "   <f1:preference>0</f1:preference>\r\n" +
          "   </f1:tel>\r\n" +
          " </f1:contact> \r\n";
  

  public static void main(String[] args) throws IOException {

    StringBuffer buf = new StringBuffer(1000);

    for (int i=1; i<501; i++) {
      buf.append(TEMPLATE.replaceAll("%I", i+""));
    }

    FileWriter wr = new FileWriter("Contacts.partial.xml");
    wr.write(buf.toString());
    wr.close();

    System.out.println("buf.toString() = " + buf.toString());

  }

}
