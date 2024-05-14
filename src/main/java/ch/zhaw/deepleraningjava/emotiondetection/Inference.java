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
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;


import javax.imageio.ImageIO;

public class Inference {

    Predictor<Image, Classifications> predictor;

    public Inference() {
        try {
            // Connect to Azure Blob Storage
            String connectionString = "DefaultEndpointsProtocol=https;AccountName=modelemotiondetection;AccountKey=44CjkRSLUVtR89bBeCnOMLOcmIA8ginEdHWApiYvOsS1G9GnKl9EF8WGWaKJtgtXPJ4IAOgWQBcF+AStwSN8CQ==;EndpointSuffix=core.windows.net";
            BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectionString).buildClient();
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient("modelemotiondetection");

            // Download model from Azure Blob Storage
            BlobClient blobClient = containerClient.getBlobClient("model.pth");
            File modelFile = new File("modelazure/model.pth");
            blobClient.downloadToFile(modelFile.getAbsolutePath());

            // Load model from the downloaded file
            Model model = Model.newInstance(connectionString);
            model.load(modelFile.toPath());

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

    public Classifications predict(byte[] image) throws ModelException, TranslateException, IOException {
        InputStream is = new ByteArrayInputStream(image);
        BufferedImage bi = ImageIO.read(is);
        Image img = ImageFactory.getInstance().fromImage(bi);

        Classifications predictResult = this.predictor.predict(img);
        return predictResult;
    }
}

