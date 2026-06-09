using System.Drawing;
using System.IO;
using System.Windows.Forms;

namespace CLIESCRITORIO.Vista
{
    internal static class UiImages
    {
        public static Image? Load(string fileName)
        {
            var path = Path.Combine(AppContext.BaseDirectory, "img", fileName);
            if (!File.Exists(path)) return null;

            using var stream = new FileStream(path, FileMode.Open, FileAccess.Read, FileShare.ReadWrite);
            using var image = Image.FromStream(stream);
            return (Image)image.Clone();
        }

        public static PictureBox Logo(string fileName, int height)
        {
            var img = Load(fileName);
            var box = new PictureBox
            {
                Image = img,
                SizeMode = PictureBoxSizeMode.Zoom,
                Height = height,
                Width = height,
                BackColor = Color.Transparent
            };

            return box;
        }
    }
}
