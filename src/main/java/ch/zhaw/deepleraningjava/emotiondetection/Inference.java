package ch.zhaw.deepleraningjava.emotiondetection;

import ai.djl.Model;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class Inference {

    private static final String MODEL_DIRECTORY = "azuremodel";
    private static final String PARAMS_FILE = "emotionclassifier-0020.params";
    private static final String SYNSET_FILE = "synset.txt";

    private Predictor<Image, Classifications> predictor;

    public Inference() {

        try {
            if (!modelFilesExist()) {
                downloadModelFiles();
            }

            // Load the model from the downloaded files
            Model model = Models.getModel();
            Path modelDir = Paths.get(MODEL_DIRECTORY);
            model.load(modelDir);

            // Define a translator for pre and post processing
            Translator<Image, Classifications> translator = ImageClassificationTranslator.builder()
                    .addTransform(new Resize(Models.IMAGE_WIDTH, Models.IMAGE_HEIGHT))
                    .addTransform(new ToTensor())
                    .optApplySoftmax(true)
                    .build();
            predictor = model.newPredictor(translator);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean modelFilesExist() {
        File paramsFile = new File(MODEL_DIRECTORY, PARAMS_FILE);
        File synsetFile = new File(MODEL_DIRECTORY, SYNSET_FILE);
        return paramsFile.exists() && synsetFile.exists();
    }

    private void downloadModelFiles() {
        try {
            // Connect to Azure Blob Storage
            String containerName = "modelemotiondetection";
            String accessKey = ("DefaultEndpointsProtocol=https;AccountName=modelemotiondetection;AccountKey=44CjkRSLUVtR89bBeCnOMLOcmIA8ginEdHWApiYvOsS1G9GnKl9EF8WGWaKJtgtXPJ4IAOgWQBcF+AStwSN8CQ==;EndpointSuffix=core.windows.net");

            BlobServiceClientBuilder serviceClientBuilder = new BlobServiceClientBuilder()
                    .connectionString(accessKey);
            BlobContainerClient blobContainerClient = serviceClientBuilder.buildClient()
                    .getBlobContainerClient(containerName);

            // Download the model files from Azure Blob Storage
            downloadModelFile(blobContainerClient, PARAMS_FILE, MODEL_DIRECTORY);
            downloadModelFile(blobContainerClient, SYNSET_FILE, MODEL_DIRECTORY);
        
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadModelFile(BlobContainerClient containerClient, String fileName, String targetDirectory) {
        BlobClient blobClient = containerClient.getBlobClient(fileName);
        blobClient.downloadToFile(targetDirectory + "/" + fileName);
    }


    public Classifications predict(byte[] image) throws ModelException, TranslateException, IOException {
        InputStream is = new ByteArrayInputStream(image);
        BufferedImage bi = ImageIO.read(is);
        Image img = ImageFactory.getInstance().fromImage(bi);

        Classifications predictResult = this.predictor.predict(img);
        return predictResult;
    }
}