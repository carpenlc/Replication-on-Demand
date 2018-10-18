package mil.nga.rod.model;

import java.sql.Date;

import org.junit.Test;
import static org.junit.Assert.*;

public class TestProduct {
    
    public static String AOR_CODE = "PACOM";
    public static String CLASSIFICATION = "U";
    public static String CLASSIFICATION_DESCRIPTION = "UNCLASSIFIED";
    public static String COUNTRY_NAME = "Brazil";
    public static String ISO3CHR = "BRA";
    public static String MEDIA_NAME = "cb01sc512I2";
    public static String NRN = "CB01USC512L";
    public static String NSN = "7644012312312";
    public static String NOTES = "Product made with best available imagery.";
    public static String PATH  = "/path/to/file/name.extension";
    public static String PRODUCT_TYPE = "CIB01";
    public static String RELEASABILITY = "DS";
    public static String RELEASABILITY_DESCRIPTION = "LIMITED DISTRIBUTION";
    public static String URL = "https://rod.geo.nga.mil/path/to/file.zip";
    
    public static long   SIZE = 12345L;
    public static long   EDITION = 2L;
    
    public static Date   FILE_DATE = new Date(System.currentTimeMillis());
    public static Date   LOAD_DATE = FILE_DATE;
    
    @Test
    public void runTest() {
        
        Product product = new Product.ProductBuilder()
                .aorCode(AOR_CODE)
                .classification(CLASSIFICATION)
                .classificationDescription(CLASSIFICATION_DESCRIPTION)
                .countryName(COUNTRY_NAME)
                .edition(EDITION)
                .fileDate(FILE_DATE)
                .iso3Char(ISO3CHR)
                .loadDate(LOAD_DATE)
                .mediaName(MEDIA_NAME)
                .notes(NOTES)
                .nsn(NSN)
                .nrn(NRN)
                .path(PATH)
                .productType(PRODUCT_TYPE)
                .releasability(RELEASABILITY)
                .releasabilityDescription(RELEASABILITY_DESCRIPTION)
                .size(SIZE)
                .url(URL)
                .build();
        
        assertEquals(product.getAorCode(), AOR_CODE);
        assertEquals(product.getClassification(), CLASSIFICATION);
        assertEquals(product.getClassificationDescription(), CLASSIFICATION_DESCRIPTION);
        assertEquals(product.getCountryName(), COUNTRY_NAME);
        assertEquals(product.getEdition(), EDITION);
        assertEquals(product.getFileDate(), FILE_DATE);
        assertEquals(product.getIso3Char(), ISO3CHR);
        assertEquals(product.getLoadDate(), LOAD_DATE);
        assertEquals(product.getMediaName(), MEDIA_NAME);
        assertEquals(product.getNotes(), NOTES);
        assertEquals(product.getNRN(), NRN);
        assertEquals(product.getNSN(), NSN);
        assertEquals(product.getPath(), PATH);
        assertEquals(product.getProductType(), PRODUCT_TYPE);
        assertEquals(product.getReleasability(), RELEASABILITY);
        assertEquals(product.getReleasabilityDescription(), RELEASABILITY_DESCRIPTION);
        assertEquals(product.getURL(), URL);
    }
    
}
