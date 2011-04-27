package j3d.primitives;

import com.sun.j3d.utils.image.TextureLoader;

public class NewTextureLoader extends TextureLoader {
    static java.awt.Component observer;

    /**
     * Specify an object to server as the image observer.
     * Use this method once before constructing any texture loaders.
     * @param imageObserver     the object to be used in subsequent
     * NewTextureLoader constuctions
     */
    public static void setImageObserver(java.awt.Component imageObserver){
        observer = imageObserver;
    }

    /**
     * Retreve the object used as the image observer for NewTextureLoader
     * objects.
     * Use this method when the image observer is needed.
     * @return the object used in as the image observer in subsequent
     * NewTextureLoader constuctions
     */
    public static java.awt.Component getImageObserver(){
        return observer;
    }

    // constructors without an image observer argument
    /**
     * Constructs a NewTextureLoader object loading the specified iamge in
     * default (RGBA) format.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param image     the image object to load
     */
    public NewTextureLoader(java.awt.Image image){
        super(image, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified image
     * and option flags in the default (RGBA) format.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param image     the image object to load
     * @param flags     the flags to use in construction (e.g. generate mipmap)
     */
    public NewTextureLoader(java.awt.Image image, int flags){
        super( image, flags, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified file
     * using the specified format.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param image     the image object to load
     * @param format    specificaiton of which channels to use (e.g. RGB)
     */
    public NewTextureLoader(java.awt.Image image, java.lang.String format){
        super( image, format, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified file
     * with specified format and flags.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param image     the image object to load
     * @param format    specificaiton of which channels to use (e.g. RGB)
     * @param flags     the flags to use in construction (e.g. generate mipmap)
     */
    public NewTextureLoader(java.awt.Image image, java.lang.String format, int flags){
        super( image, format, flags, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified file
     * using the default format (RGBA).
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param fname     the name of the file to load
     */
    public NewTextureLoader(java.lang.String fname){
        super( fname, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified file
     * with the specified flags.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param fname     the name of the file to load
     * @param flags     the flags to use in construction (e.g. generate mipmap)
     */
    public NewTextureLoader(java.lang.String fname, int flags){
        super( fname, flags, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified file
     * using the specified format.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param fname     the name of the file to load
     * @param format    specificaiton of which channels to use (e.g. RGB)
     */
    public NewTextureLoader(java.lang.String fname, java.lang.String format){
        super( fname, format, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified file
     * using the specified format and flags.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param fname     the name of the file to load
     * @param format    specificaiton of which channels to use (e.g. RGB)
     * @param flags     the flags to use in construction (e.g. generate mipmap)
     */
    public NewTextureLoader(java.lang.String fname, java.lang.String format, int flags){
        super( fname, format, flags, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified URL
     * using the default format.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param url       specifies the URL of the image to load
     */
    public NewTextureLoader(java.net.URL url){
        super(url, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified URL
     * using the specified flags.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param url       specifies the URL of the image to load
     * @param flags     the flags to use in construction (e.g. generate mipmap)
     */
    public NewTextureLoader(java.net.URL url, int flags){
        super(url, flags, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified URL
     * using the specified format.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param url       specifies the URL of the image to load
     * @param format    specificaiton of which channels to use (e.g. RGB)
     */
    public NewTextureLoader(java.net.URL url, java.lang.String format){
        super(url, format, observer);
    }

    /**
     * Constructs a NewTextureLoader object loading the specified URL
     * using the specified format and flags.
     * The an image observer must be set using the setImageObserver() method
     * before using this constructor.
     * @param url       specifies the URL of the image to load
     * @param format    specificaiton of which channels to use (e.g. RGB)
     * @param flags     the flags to use in construction (e.g. generate mipmap)
     */
    public NewTextureLoader(java.net.URL url, java.lang.String format, int flags){
        super(url, format, flags, observer);
    }
    
}
