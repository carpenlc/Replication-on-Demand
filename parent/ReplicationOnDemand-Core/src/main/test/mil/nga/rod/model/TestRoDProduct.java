package mil.nga.rod.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import mil.nga.rod.model.Artwork.ArtworkBuilder;

public class TestRoDProduct {

	public static String ARTWORK_PATH = "/path/to/file/name.extension";
	public static long   ARTWORK_SIZE = 1234;
	public static String CD_NAME      = "ABC12345DEF";
	public static String PATH_TO_SMALL_IMAGE     = "/path/to/file/small_file.jpg";
	public static String URL_TO_SMALL_IMAGE      = "https://some.url/path/to/small_file.jpg";
	public static String PATH_TO_THUMBNAIL_IMAGE = "/path/to/file/thumbnail_file.jpg";
	public static String URL_TO_THUMBNAIL_IMAGE  = "https://some.url/path/to/thumbnail_file.jpg";
	public static String PATH_TO_SOURCE_IMAGE    = "/path/to/file/source_file.pdf";
	public static String URL_TO_SOURCE_IMAGE     = "https://some.url/path/to/source_file.pdf";
	
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
	    
	    QueryRequestAccelerator queryRecAcc = new QueryRequestAccelerator.QueryRequestAcceleratorBuilder()
	    		.product(product)
	    		.size(TestQueryRequestAccelerator.SIZE)
	    		.fileDate(TestQueryRequestAccelerator.CURRENT_DATE)
	    		.hash(TestQueryRequestAccelerator.HASH)
	    		.build();
	    
	    ArtworkRow row = new ArtworkRow.ArtworkBuilder()
	            .nsn(TestProduct.NSN)
	            .nrn(TestProduct.NRN)
	    		.path(ARTWORK_PATH)
	    		.size(ARTWORK_SIZE)
	    		.cdName(CD_NAME)
	    		.build();
	    
	    Artwork art = new Artwork.ArtworkBuilder()
	    		.artworkRow(row)
	    		.smallImagePath(PATH_TO_SMALL_IMAGE) 
    			.smallImageUrl(URL_TO_SMALL_IMAGE)
    			.sourceImagePath(PATH_TO_SOURCE_IMAGE)
    			.sourceImageUrl(URL_TO_SOURCE_IMAGE)
    			.thumbnailImagePath(PATH_TO_THUMBNAIL_IMAGE)
    			.thumbnailImageUrl(URL_TO_THUMBNAIL_IMAGE)
    			.build();
	    		
	    RoDProduct rodProduct = new RoDProduct.RoDProductBuilder()
	    		.product(product)
	    		.queryRequestAccelerator(queryRecAcc)
	    		.artwork(art)
	    		.build();
	    
	    assertEquals(rodProduct.getArtworkPath(), ARTWORK_PATH);
	    assertEquals(rodProduct.getArtworkSize(), ARTWORK_SIZE);
	    assertEquals(rodProduct.getCdName(),      CD_NAME);
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
	}
}
