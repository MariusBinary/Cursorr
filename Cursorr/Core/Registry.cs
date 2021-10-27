using Microsoft.Win32;

namespace ImmersiveLights.Core
{
    public static class Registry
    {
        private static string USER_KEY = "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run";
        private static string APP_NAME = "Cursorr";

        /// <summary>
        /// Inserisce il percorso dell'applicazione all'interno del registro di sistema, in modo
        /// che venga eseguita all'avvio di Windows.
        /// </summary>
        public static bool AddToStartup()
        {
            if (IsAddedToStartup()) return true;

            try
            {
                using (RegistryKey key = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(USER_KEY, true))
                {
                    string executablePath = System.Reflection.Assembly.GetExecutingAssembly().Location;
                    key.SetValue(APP_NAME, $"{executablePath} -autorun");
                    return true;
                }
            }
            catch 
            {
                return false;
            }
        }
        /// <summary>
        /// Rimuove il percorso dell'applicazione dall'interno del registro di sistema, in modo
        /// che non venga eseguita all'avvio di Windows.
        /// </summary>
        public static bool RemoveFromStartup()
        {
            if (!IsAddedToStartup()) return false;

            try
            {
                using (RegistryKey key = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(USER_KEY, true))
                {
                    key.DeleteValue(APP_NAME, false);
                    return true;
                }
            }
            catch
            {
                return false;
            }
        }
        /// <summary>
        /// Aggiunge l'applicazione all'avvio di Windows o la rimuove se già presente.
        /// </summary>
        public static bool ToggleStartup()
        {
            if (!IsAddedToStartup()) {
                AddToStartup();
                return true;
            } else {
                RemoveFromStartup();
                return false;
            }
        }
        /// <summary>
        /// Verifica se il percorso dell'applicazione è già presente all'interno del registro
        /// di sistema, e se è impostato per essere eseguito all'avvio di Windows.
        /// </summary>
        public static bool IsAddedToStartup()
        {
            try
            {
                using (RegistryKey key = Microsoft.Win32.Registry.CurrentUser.OpenSubKey(USER_KEY, true))
                {
                    return key.GetValue(APP_NAME) != null ? true : false;
                }
            }
            catch
            {
                return false;
            }
        }
    }
}
