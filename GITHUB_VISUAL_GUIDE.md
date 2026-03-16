# 📱 FocusApp - GitHub APK Building Guide (Visual)

## 🎯 What You're Setting Up

```
┌─────────────────────────────────────────────────────┐
│                   YOUR COMPUTER                      │
│  ┌──────────────────────────────────────────────┐   │
│  │  FocusApp Code (in a folder)                 │   │
│  │  - MainActivity.kt                           │   │
│  │  - Games (HTML files)                        │   │
│  │  - Database                                  │   │
│  │  - etc.                                      │   │
│  └──────────────────────────────────────────────┘   │
│                      │                               │
│                      │ git push                      │
│                      ▼                               │
└─────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│                  GITHUB.COM                          │
│  ┌──────────────────────────────────────────────┐   │
│  │  Your GitHub Repository                      │   │
│  │  (Cloud storage for code)                    │   │
│  └──────────────────────────────────────────────┘   │
│                      │                               │
│                      │ Triggers (automatically)     │
│                      ▼                               │
│  ┌──────────────────────────────────────────────┐   │
│  │  GitHub Actions (CI/CD Pipeline)             │   │
│  │  ├─ Sets up Java                             │   │
│  │  ├─ Runs Gradle build                        │   │
│  │  └─ Creates APK                              │   │
│  └──────────────────────────────────────────────┘   │
│                      │                               │
│                      ▼                               │
│  ┌──────────────────────────────────────────────┐   │
│  │  APK Ready for Download!                     │   │
│  │  ✅ focusapp-debug.apk                       │   │
│  │  ✅ focusapp-release-unsigned.apk            │   │
│  └──────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────────────┐
│                   YOUR PHONE                         │
│            (Download and install APK)               │
└─────────────────────────────────────────────────────┘
```

---

## 📋 5-Minute Setup Process

### Step 1: Create GitHub Repository (2 min)

**On GitHub.com:**

```
Visit: https://github.com/new

Fill in form:
┌────────────────────────────────────┐
│ Repository name: focusapp           │
│ Description: Android focus app      │
│ Public / Private: Your choice       │
│ [Create repository button]          │
└────────────────────────────────────┘

Result: You get a GitHub repository
        https://github.com/YOUR_USERNAME/focusapp
```

---

### Step 2: Copy GitHub URL (10 sec)

**On your new GitHub repository page:**

Look for green button labeled **"< > Code"**

```
┌─────────────────────────────────────┐
│ <> Code ▼                           │
├─────────────────────────────────────┤
│ HTTPS: https://github.com/YOUR...   │ ← Copy this
│ SSH: git@github.com:YOUR...         │
│ GitHub CLI: gh repo clone YOUR...   │
└─────────────────────────────────────┘
```

Copy the HTTPS URL (starts with https://)

---

### Step 3: Open Terminal in FocusApp Folder (30 sec)

**On your computer:**

```
📁 FocusApp/
   ├── build.gradle
   ├── app/
   ├── .github/  ← Already created!
   ├── .gitignore ← Already created!
   ├── README.md
   └── [other files]

Open Terminal/Command Prompt in this folder
```

**Windows:**
- Right-click in folder → "Open PowerShell here"
- Or: Right-click → Git Bash Here

**Mac/Linux:**
- Right-click → "New Terminal at Folder"
- Or: `cd /path/to/FocusApp`

---

### Step 4: Run Git Commands (2 min)

**Copy & paste these one by one:**

```bash
# Initialize git
git init

# Add all files
git add .

# Create commit
git commit -m "Initial commit: FocusApp with all 6 features"

# Add remote (replace YOUR_USERNAME!)
git remote add origin https://github.com/YOUR_USERNAME/focusapp.git

# Set default branch to main
git branch -M main

# Push to GitHub
git push -u origin main
```

**What happens:**
- Terminal might ask for GitHub password/token
- Files upload to GitHub (~2 minutes)
- You'll see: "Everything up-to-date"

---

### Step 5: Watch It Build! (3-5 min)

**On GitHub:**

1. Go to: https://github.com/YOUR_USERNAME/focusapp
2. Click: **"Actions"** tab (top menu)
3. Watch: "Build APK" workflow running

```
┌──────────────────────────────────┐
│ Build APK  [Running ●●●●]        │
├──────────────────────────────────┤
│ ✓ Set up job                     │
│ ✓ Set up JDK 11                  │
│ ✓ Make gradlew executable        │
│ ⏳ Build Debug APK                │
│ ⏳ Build Release APK               │
│ ⏳ Upload artifacts                │
│ ? Complete (waiting...)           │
└──────────────────────────────────┘
```

**Builds take 5-10 minutes**

---

## 📥 Download Your APK

### Once Build is Complete:

```
GitHub Repository Page
    │
    ├─ Actions Tab
    │  │
    │  └─ Build APK (Latest)
    │     │
    │     └─ Artifacts Section
    │        │
    │        ├─ focusapp-debug.apk ← Download this
    │        └─ focusapp-release-unsigned.apk
    │
    └─ [APK saved to your computer!]
```

**Click and download either:**
- `focusapp-debug.apk` ← For testing
- `focusapp-release-unsigned.apk` ← For release

---

## 📱 Install APK on Phone

### 1. Transfer to Phone

**Via USB:**
```
1. Connect phone to computer
2. Drag APK file to phone
3. File appears in Downloads folder
```

**Via Email/Cloud:**
```
1. Email APK to yourself
2. Download on phone
3. Or use Google Drive
```

### 2. Enable Unknown Sources

**Settings → Security → Unknown Sources**

```
Allow installation from unknown sources
[Enable this setting]
```

### 3. Install APK

**In File Manager:**
```
1. Navigate to Downloads
2. Tap the APK file
3. Tap "Install"
4. Wait for installation
5. "App installed" message
```

### 4. Open & Setup App

```
1. Find FocusApp in apps
2. Tap to open
3. Go to Settings → Enable Accessibility Service
4. Android Settings opens → Toggle ON
5. Back to app
6. Create first focus schedule!
```

---

## 🔄 Update Workflow (Ongoing)

### Every Time You Make Changes:

```
You modify code (e.g., add new game):

  ┌─────────────────────────────┐
  │ Edit file (e.g., MainActivity.kt) │
  └─────────────────────────────┘
           │
           ▼
  ┌─────────────────────────────┐
  │ git add .                   │
  │ git commit -m "Description"│
  │ git push origin main        │
  └─────────────────────────────┘
           │
           ▼
  GitHub Actions AUTOMATICALLY:
  ✅ Builds APK
  ✅ Uploads artifacts
  ✅ You download new APK

[5-10 minutes later: New APK ready!]
```

---

## 📊 Example Timeline

```
9:00 AM  - You push code to GitHub
           └─ "git push origin main"

9:02 AM  - GitHub Actions starts build
           └─ Email notification (optional)

9:07 AM  - Build completes ✅
           └─ APK ready in Artifacts

9:08 AM  - You download new APK
           └─ Click "focusapp-debug.apk"

9:10 AM  - Transfer to phone & install
           └─ Test new features!

9:15 AM  - Make improvements, repeat!
```

---

## 🚀 Creating Releases (Optional)

**For official versions (v1.0.0, v1.1.0, etc.):**

```bash
# Create version tag
git tag v1.0.0

# Push tag to GitHub
git push origin v1.0.0
```

**GitHub automatically:**
1. Builds APK
2. Creates Release page
3. Attaches APK for download

**Result:** Professional release page
```
https://github.com/YOUR_USERNAME/focusapp/releases/tag/v1.0.0
↓
[Download focusapp-release-unsigned.apk directly from release]
```

---

## ✅ Verification Checklist

```
Setup Checklist:

□ GitHub account created (github.com/signup)
□ Repository created at github.com/new
□ FocusApp folder on your computer
□ Terminal opened in FocusApp folder
□ Ran: git init
□ Ran: git add .
□ Ran: git commit -m "..."
□ Ran: git remote add origin https://...
□ Ran: git push -u origin main
□ GitHub repository shows "FocusApp" files
□ Actions tab shows "Build APK" running
□ Build completed (green checkmark)
□ Artifacts available for download
□ APK downloaded to computer
□ APK transferred to phone
□ Unknown Sources enabled in phone
□ APK installed on phone
□ App opens successfully
□ Accessibility Service enabled
□ First focus schedule created
```

---

## 🎯 Common Actions

| What You Want | What To Do |
|---------------|-----------|
| **Build new APK** | `git push origin main` |
| **See build status** | Go to GitHub → Actions tab |
| **Download APK** | Actions → [latest build] → Artifacts |
| **Update app** | Edit code → git add/commit/push |
| **Create release** | `git tag v1.0.0` → `git push origin v1.0.0` |
| **Share APK with friends** | Send GitHub Releases link |
| **Fix bug** | Edit file → push → auto-builds |

---

## 💡 Pro Tips

### Tip 1: Commit Messages
```
❌ Bad:     git commit -m "update"
✅ Good:    git commit -m "Add memory game feature"
✅ Better:  git commit -m "Add: Memory game, improve UI colors"
```

### Tip 2: Frequent Commits
```
Commit after each small change:
- New feature
- Bug fix
- Code cleanup

This keeps history clean and helps if something breaks!
```

### Tip 3: Use Branches for Big Changes
```
git checkout -b feature/new-game
git add .
git commit -m "WIP: Adding new game feature"
git push origin feature/new-game

Then create Pull Request on GitHub to merge!
```

### Tip 4: Check Build Status Before Downloading
```
Green checkmark ✅ = Build succeeded
Red X ❌ = Build failed (don't download)
Yellow circle ⏳ = Still building
```

---

## 🆘 If Build Fails

**Build failed (red X)?**

```
1. Click the failed "Build APK" workflow
2. Scroll down to see error message
3. Common causes:
   ✓ Syntax error in code (extra comma, etc.)
   ✓ Missing file (accidentally deleted something)
   ✓ Wrong Android SDK version

Fix the error, commit, and push again:
git add .
git commit -m "Fix build error"
git push origin main

GitHub will rebuild automatically!
```

---

## 🔐 Security

**What GitHub Actions CAN'T access:**
- ✅ Your computer files (only what you push)
- ✅ Your GitHub secrets (unless explicitly added)
- ✅ Your passwords
- ✅ Your signing keys

**Keep safe:**
- ✅ Never commit passwords
- ✅ Never commit API keys
- ✅ Use `.gitignore` for secrets (already done!)
- ✅ Use GitHub Secrets for any sensitive data

---

## 🎓 Summary

```
Before GitHub:
  └─ Build APK locally with Android Studio (30 min)

After GitHub:
  └─ Push code → GitHub builds automatically (5 min)
     └─ Download APK → Install on phone (2 min)

You saved: 25 minutes per build! 🚀
```

---

## 🎉 You're Ready!

You now have:
```
✅ Professional CI/CD pipeline
✅ Automatic APK builds in cloud
✅ Easy APK sharing
✅ Build history saved
✅ Zero local build complexity
```

**Next: Go create that GitHub repository!** 🚀

---

**Links:**
- GitHub: https://github.com
- Git Tutorial: https://git-scm.com/book
- Actions Docs: https://docs.github.com/actions

Happy building! 📱💪
