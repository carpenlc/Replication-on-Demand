package mil.nga.rod.model;

import java.util.Optional;

/**
 * Adaptor class allowing us to decorate the <code>ArtworkRow</code> object 
 * with information associated with the scaled images.
 * 
 * @author L. Craig Carpenter
 */
public class ArtworkDecorator {

	/**
	 * The Product that will have the decorations applied.
	 */
	protected Optional<ArtworkRow> artwork;
	
	/**
	 * Default constructor required by JAX-B.
	 */
	public ArtworkDecorator() {}
	
	/**
	 * Constructor for the ArtworkDecorator data.
	 * @param prod The ArtworkDecorator data.
	 */
	protected ArtworkDecorator(ArtworkRow value) {
		if (value == null) {
			artwork = Optional.empty();
		}
		else {
			artwork = Optional.of(value);
		}
	}
	
	/**
	 * Accessor method for the ArtworkDecorator data.
	 * @return The ArtworkDecorator data.
	 */
	public ArtworkRow getArtworkRow() throws IllegalStateException {
		if (artwork.isPresent()) {
			return artwork.get();
		}
		else {
			throw new IllegalStateException("Required ArtworkRow "
					+ "object not provided.");
			}
	}
	
	/**
	 * Mutator method for the internal <code>ArtworkRow</code> object.
	 * @param The object handle
	 */
	public void setArtworkRow(ArtworkRow value) {
		if (value == null) {
			artwork = Optional.empty();
		}
		else {
			artwork = Optional.of(value);
		}
	}
	
	/**
	 * Convert to human-readable String.
	 */
	public String toString() {
		if (artwork.isPresent()) {
			return artwork.get().toString();
		}
		else {
			return "[ parent ArtworkRow object not available ]";
		}
	}
}
