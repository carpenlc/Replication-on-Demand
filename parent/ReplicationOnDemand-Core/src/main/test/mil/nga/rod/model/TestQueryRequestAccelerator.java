package mil.nga.rod.model;

import java.util.Date;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class ensuring that the target POJO reads/writes correctly.
 * 
 * @author L. Craig Carpenter
 *
 */
public class TestQueryRequestAccelerator { 
    
	public static final String HASH = "0123456789ABCDEF";
	public static final Date   CURRENT_DATE =  new Date(System.currentTimeMillis());
	public static final long   SIZE = 12345L;
	
    /**
     * Test construction of the target object.
     */
    @Test
    public void runTest() {
        
        Product product = new Product.ProductBuilder()
                .aorCode(TestProduct.AOR_CODE)
                .classification(TestProduct.CLASSIFICATION)
                .classificationDescription(TestProduct.CLASSIFICATION_DESCRIPTION)
                .countryName(TestProduct.COUNTRY_NAME)
                .edition(TestProduct.EDITION)
                .fileDate(TestProduct.FILE_DATE)
                .iso3Char(TestProduct.ISO3CHR)
                .loadDate(TestProduct.LOAD_DATE)
                .mediaName(TestProduct.MEDIA_NAME)
                .notes(TestProduct.NOTES)
                .nsn(TestProduct.NSN)
                .nrn(TestProduct.NRN)
                .path(TestProduct.PATH)
                .productType(TestProduct.PRODUCT_TYPE)
                .releasability(TestProduct.RELEASABILITY)
                .releasabilityDescription(TestProduct.RELEASABILITY_DESCRIPTION)
                .size(TestProduct.SIZE)
                .url(TestProduct.URL)
                .build();
        
        QueryRequestAccelerator record = new QueryRequestAccelerator.QueryRequestAcceleratorBuilder()
        		.product(product)
        		.size(SIZE)
        		.fileDate(CURRENT_DATE)
        		.hash(HASH)
        		.build();
        
        assertEquals(record.getHash(),     HASH);
        assertEquals(record.getPath(),     TestProduct.PATH);
        assertEquals(record.getSize(),     SIZE);
        assertEquals(record.getFileDate(), CURRENT_DATE);
        assertEquals(record.getProduct().getAorCode(), TestProduct.AOR_CODE);
        assertEquals(record.getProduct().getClassification(), TestProduct.CLASSIFICATION);
        assertEquals(record.getProduct().getClassificationDescription(), TestProduct.CLASSIFICATION_DESCRIPTION);
        assertEquals(record.getProduct().getCountryName(), TestProduct.COUNTRY_NAME);
        assertEquals(record.getProduct().getEdition(), TestProduct.EDITION);
        assertEquals(record.getProduct().getFileDate(), TestProduct.FILE_DATE);
        assertEquals(record.getProduct().getIso3Char(), TestProduct.ISO3CHR);
        assertEquals(record.getProduct().getLoadDate(), TestProduct.LOAD_DATE);
        assertEquals(record.getProduct().getMediaName(), TestProduct.MEDIA_NAME);
        assertEquals(record.getProduct().getNotes(), TestProduct.NOTES);
        assertEquals(record.getProduct().getNRN(), TestProduct.NRN);
        assertEquals(record.getProduct().getNSN(), TestProduct.NSN);
        assertEquals(record.getProduct().getPath(), TestProduct.PATH);
        assertEquals(record.getProduct().getProductType(), TestProduct.PRODUCT_TYPE);
        assertEquals(record.getProduct().getReleasability(), TestProduct.RELEASABILITY);
        assertEquals(record.getProduct().getReleasabilityDescription(), TestProduct.RELEASABILITY_DESCRIPTION);
        assertEquals(record.getProduct().getURL(), TestProduct.URL);
    }
    
}
