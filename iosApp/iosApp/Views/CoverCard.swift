import SwiftUI

struct CoverCard: View {
    let name: String
    let imageUrl: String
    var subtitle: String?

    var body: some View {
        VStack(alignment: .leading, spacing: CustomStyle.spacing(.narrow)) {
            AsyncImage(url: URL(string: imageUrl)) { image in
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            } placeholder: {
                Color.secondary.opacity(0.2)
            }
            .aspectRatio(1, contentMode: .fit)
            .clipShape(RoundedRectangle(cornerRadius: CustomStyle.cornerRadius(.large)))

            VStack(alignment: .leading, spacing: CustomStyle.spacing(.tiny)) {
                Text(name)
                    .customStyle(.mediumFont)
                    .fontWeight(.bold)
                    .lineLimit(2)

                if let subtitle {
                    Text(subtitle)
                        .customStyle(.smallFont)
                        .foregroundStyle(.secondary)
                        .lineLimit(2)
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)
        }
    }
}
