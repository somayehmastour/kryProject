package se.kry.codetest;

/**
 * This is a Simple POJO class, use to map object to JSON and vice versa
 *
 */
public class URL {

	private int id;
	private String url;
	private String date;
	private String status;

	public URL(int id, String url, String date, String status) {
		super();
		this.id = id;
		this.url = url;
		this.date = date;
		this.status = status;
	}

	public URL() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "URL [id=" + id + ", url=" + url + ", date=" + date + ", status=" + status + "]";
	}

}
