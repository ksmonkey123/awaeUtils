package ch.awae.utils.source;

import java.io.FileInputStream;
import java.io.InputStream;

import ch.awae.utils.functional.Result;


final class FileSource extends Source {

	private final String filename;

	FileSource(String filename) {
		this.filename = filename;
	}

	@Override
	public Result<InputStream> mkStream() {
		return Result.eval(() -> new FileInputStream(this.filename));
	}

	@Override
	public String toString() {
		return "File Source ( " + this.filename + " )";
	}

}
