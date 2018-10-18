package mil.nga.rod;


import static org.junit.Assert.*;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import mil.nga.rod.JSONSerializer;
import mil.nga.rod.model.Artwork;
import mil.nga.rod.model.ArtworkRow;
import mil.nga.rod.model.Product;
import mil.nga.rod.model.QueryRequestAccelerator;
import mil.nga.rod.model.RoDProduct;
import mil.nga.rod.model.TestProduct;
import mil.nga.rod.model.TestRoDProduct;
import mil.nga.rod.model.TestQueryRequestAccelerator;

public class TestJSONSerializer {

    private static final DateFormat dateFormatter = 
            new SimpleDateFormat("yyyy-MM-dd");
    
    
    @Test
    public void testStringListSerialization() {
        
        List<String> list = new ArrayList<String>();
        list.add("Element 1");
        list.add("Element 2");
        list.add("Element 3");
        list.add("Element 4");
        list.add("Element 5");
        list.add("Element 6");
        list.add("Element 7");
        
        String serialized = JSONSerializer.getInstance().serialize(list);
        List<String> list2 = JSONSerializer
                    .getInstance()
                    .deserializeToStringList(serialized);
        
        assertTrue(list.equals(list2));
        
    }
    
    @Test
    public void testQueryRequestAcceleratorSerialization() {

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
        		.size(TestQueryRequestAccelerator.SIZE)
        		.fileDate(TestQueryRequestAccelerator.CURRENT_DATE)
        		.hash(TestQueryRequestAccelerator.HASH)
        		.build();
        
        String serialized = JSONSerializer.getInstance().serialize(record);
        QueryRequestAccelerator record2 = JSONSerializer
                    .getInstance()
                    .deserializeToQueryRequestAccelerator(serialized);
        
        System.out.println(serialized);
        // System.out.println(dateFormatter.format(record.getFileDate()));
        // System.out.println(dateFormatter.format(record2.getFileDate()));
        
        // Compare the strings.
        assertEquals(dateFormatter.format(record.getFileDate()), 
                dateFormatter.format(record2.getFileDate()));
        assertEquals(record.getHash(), record2.getHash());
        assertEquals(record.getPath(), record2.getPath());
        assertEquals(record.getSize(), record2.getSize());
        
    }
    
    @Test
    public void testRoDProductSerialization() {
    	
    	
    	
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
	    
	    QueryRequestAccelerator queryRecAcc = new QueryRequestAccelerator.QueryRequestAcceleratorBuilder()
	    		.product(product)
	    		.size(TestQueryRequestAccelerator.SIZE)
	    		.fileDate(TestQueryRequestAccelerator.CURRENT_DATE)
	    		.hash(TestQueryRequestAccelerator.HASH)
	    		.build();
	    
	    ArtworkRow row = new ArtworkRow.ArtworkBuilder()
	            .nsn(TestProduct.NSN)
	            .nrn(TestProduct.NRN)
	    		.path(TestRoDProduct.ARTWORK_PATH)
	    		.size(TestRoDProduct.ARTWORK_SIZE)
	    		.cdName(TestRoDProduct.CD_NAME)
	    		.build();
	    
	    Artwork art = new Artwork.ArtworkBuilder()
	    		.artworkRow(row)
	    		.smallImagePath(TestRoDProduct.PATH_TO_SMALL_IMAGE) 
    			.smallImageUrl(TestRoDProduct.URL_TO_SMALL_IMAGE)
    			.sourceImagePath(TestRoDProduct.PATH_TO_SOURCE_IMAGE)
    			.sourceImageUrl(TestRoDProduct.URL_TO_SOURCE_IMAGE)
    			.thumbnailImagePath(TestRoDProduct.PATH_TO_THUMBNAIL_IMAGE)
    			.thumbnailImageUrl(TestRoDProduct.URL_TO_THUMBNAIL_IMAGE)
    			.build();
	    		
	    RoDProduct rodProduct = new RoDProduct.RoDProductBuilder()
	    		.product(product)
	    		.queryRequestAccelerator(queryRecAcc)
	    		.artwork(art)
	    		.build();
	    
	    assertEquals(rodProduct.getArtworkPath(), TestRoDProduct.ARTWORK_PATH);
	    assertEquals(rodProduct.getArtworkSize(), TestRoDProduct.ARTWORK_SIZE);
	    assertEquals(rodProduct.getCdName(),      TestRoDProduct.CD_NAME);
        assertEquals(rodProduct.getHash(),        TestQueryRequestAccelerator.HASH);
        assertEquals(rodProduct.getPath(),        TestProduct.PATH);
        assertEquals(rodProduct.getSize(),        TestQueryRequestAccelerator.SIZE);
        //assertEquals(rodProduct.getFileDate(),    TestQueryRequestAccelerator.CURRENT_DATE);
        //assertEquals(record.getProduct().getAorCode(), TestProduct.AOR_CODE);
        assertEquals(rodProduct.getClassification(), TestProduct.CLASSIFICATION);
        assertEquals(rodProduct.getClassificationDescription(), TestProduct.CLASSIFICATION_DESCRIPTION);
        //assertEquals(record.getProduct().getCountryName(), TestProduct.COUNTRY_NAME);
        assertEquals(rodProduct.getEdition(), TestProduct.EDITION);
        //assertEquals(record.getProduct().getFileDate(), TestProduct.FILE_DATE);
        //assertEquals(rodProduct.getIso3Char(), TestProduct.ISO3CHR);
        assertEquals(rodProduct.getLoadDate(), TestProduct.LOAD_DATE);
        assertEquals(rodProduct.getMediaName(), TestProduct.MEDIA_NAME);
        assertEquals(rodProduct.getNotes(), TestProduct.NOTES);
        assertEquals(rodProduct.getNRN(), TestProduct.NRN);
        assertEquals(rodProduct.getNSN(), TestProduct.NSN);
        //assertEquals(record.getProduct().getPath(), TestProduct.PATH);
        assertEquals(rodProduct.getProductType(), TestProduct.PRODUCT_TYPE);
        assertEquals(rodProduct.getReleasability(), TestProduct.RELEASABILITY);
        assertEquals(rodProduct.getReleasabilityDescription(), TestProduct.RELEASABILITY_DESCRIPTION);
        assertEquals(rodProduct.getURL(), TestProduct.URL);
    	
	    
	    String serialized = JSONSerializer.getInstance().serialize(rodProduct);
	    System.out.println(serialized);
	    RoDProduct rodProduct2 = JSONSerializer
                    .getInstance()
                    .deserializeToRoDProduct(serialized);
        
	    assertEquals(rodProduct2.getArtworkPath(), TestRoDProduct.ARTWORK_PATH);
	    assertEquals(rodProduct2.getArtworkSize(), TestRoDProduct.ARTWORK_SIZE);
	    assertEquals(rodProduct2.getCdName(),      TestRoDProduct.CD_NAME);
        assertEquals(rodProduct2.getHash(),        TestQueryRequestAccelerator.HASH);
        assertEquals(rodProduct2.getPath(),     TestProduct.PATH);
        assertEquals(rodProduct2.getSize(), TestQueryRequestAccelerator.SIZE);
        assertEquals(dateFormatter.format(rodProduct.getFileDate()), 
                dateFormatter.format(rodProduct2.getFileDate()));
        //assertEquals(record.getProduct().getAorCode(), TestProduct.AOR_CODE);
        assertEquals(rodProduct2.getClassification(), TestProduct.CLASSIFICATION);
        assertEquals(rodProduct2.getClassificationDescription(), TestProduct.CLASSIFICATION_DESCRIPTION);
        //assertEquals(record.getProduct().getCountryName(), TestProduct.COUNTRY_NAME);
        assertEquals(rodProduct2.getEdition(), TestProduct.EDITION);
        //assertEquals(record.getProduct().getFileDate(), TestProduct.FILE_DATE);
        //assertEquals(rodProduct2.getIso3Char(), TestProduct.ISO3CHR);
        assertEquals(dateFormatter.format(rodProduct.getLoadDate()), 
                dateFormatter.format(rodProduct2.getLoadDate()));
        assertEquals(rodProduct2.getMediaName(), TestProduct.MEDIA_NAME);
        assertEquals(rodProduct2.getNotes(), TestProduct.NOTES);
        assertEquals(rodProduct2.getNRN(), TestProduct.NRN);
        assertEquals(rodProduct2.getNSN(), TestProduct.NSN);
        //assertEquals(record.getProduct().getPath(), TestProduct.PATH);
        assertEquals(rodProduct2.getProductType(), TestProduct.PRODUCT_TYPE);
        assertEquals(rodProduct2.getReleasability(), TestProduct.RELEASABILITY);
        assertEquals(rodProduct2.getReleasabilityDescription(), TestProduct.RELEASABILITY_DESCRIPTION);
        assertEquals(rodProduct2.getURL(), TestProduct.URL);
        
        System.out.println(rodProduct2.toString());
    }
 
    
}
