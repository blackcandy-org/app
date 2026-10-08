import SwiftUI
import LNPopupUI

struct MainScreen: View {
    var body: some View {
        TabView {
            Tab("label.home", systemImage: "house") {
                NavigationStack {
                    HomeScreen()
                        .navigationBarTitleDisplayMode(.inline)
                }
            }

            Tab("label.library", systemImage: "square.stack") {
                NavigationStack {
                    LibraryScreen()
                        .navigationBarTitleDisplayMode(.inline)
                }
            }
        }
        .popup(isBarPresented: .constant(true)) {
            PlayerScreen()
        }
    }
}
