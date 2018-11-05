package mil.nga.rod;

import java.sql.Date;

import org.junit.Test;

import mil.nga.artwork.ArtworkBuilder;
import mil.nga.artwork.ArtworkProcessor;
import mil.nga.rod.model.Artwork;
import mil.nga.rod.model.ArtworkRow;
import mil.nga.rod.model.Product;

public class TestArtworkProcessor {

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
	public void testConstruction() throws Exception {
		try {
			
			ArtworkRow row = new ArtworkRow.ArtworkBuilder()
					.nrn("CB01UND443B")
					.nsn("7644015861357")
					.size(new Long(346994147))
					.path("/tmp/cb01no445j1_artwork.zip")
					.cdName("cb01nd443b1")
					.build();
			
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
			
			Artwork art = new ArtworkBuilder()
							.product(product)
							.build();
			ArtworkProcessor processor = new ArtworkProcessor();
			processor.process(art);
			
			System.out.println("Done");
			//System.out.println(processor.getBaseOutputPath());
			//System.out.println(processor.getOutputPath());
			//System.out.println(processor.getBaseFilename());
			//System.out.println(processor.getThumbnailImagePath());
			//System.out.println(processor.getThumbnailImageUrl());
			//System.out.println(processor.getSmallImagePath());
			//System.out.println(processor.getSmallImageUrl());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
