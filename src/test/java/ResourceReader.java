import com.baomibing.work.exception.ExceptionEnum;
import com.baomibing.work.exception.WorkFlowException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * ResourceReader helper for reading json file
 *
 * @author zening
 * @version 1.0.0
 **/
public class ResourceReader {

    public static String readJSON(String fileName) {
        try {
            URL resource = ResourceReader.class.getResource(fileName);
            byte[] bytes = Files.readAllBytes(Paths.get(resource.toURI()));
            return new String(bytes);
        } catch (URISyntaxException | IOException e) {
            throw new WorkFlowException(ExceptionEnum.CAN_NOT_LOAD_RESOURCE_OF_NAME, fileName);
        }
    }
}
