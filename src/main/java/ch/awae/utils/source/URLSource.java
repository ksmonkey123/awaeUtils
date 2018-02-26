package ch.awae.utils.source;

import java.io.InputStream;
import java.net.URL;

import ch.awae.utils.functional.Result;

final class URLSource extends Source {

	private final String url;

	URLSource(String url) {
		this.url = url;
	}

	@Override
	public Result<InputStream> mkStream() {
		return Result.of(this.url).map(URL::new).map(URL::openStream);
	}

	@Override
	public String toString() {
		return "URL Source ( " + this.url + " )";
	}

}
