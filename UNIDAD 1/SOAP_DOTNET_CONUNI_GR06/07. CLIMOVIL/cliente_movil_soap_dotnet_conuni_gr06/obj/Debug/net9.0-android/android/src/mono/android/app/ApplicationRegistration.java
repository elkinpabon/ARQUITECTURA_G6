package mono.android.app;

public class ApplicationRegistration {

	public static void registerApplications ()
	{
				// Application and Instrumentation ACWs must be registered first.
		mono.android.Runtime.register ("Ec.Edu.Monster.MainApplication, cliente_movil_soap_dotnet_conuni_gr06, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", crc645056a4a899bc0bbe.MainApplication.class, crc645056a4a899bc0bbe.MainApplication.__md_methods);
		mono.android.Runtime.register ("Microsoft.Maui.MauiApplication, Microsoft.Maui, Version=1.0.0.0, Culture=neutral, PublicKeyToken=null", crc6488302ad6e9e4df1a.MauiApplication.class, crc6488302ad6e9e4df1a.MauiApplication.__md_methods);
		
	}
}
