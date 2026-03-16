# 📱 Build FocusApp APK Using GitHub Actions

## 🎯 Overview

Instead of building on your computer, GitHub Actions will **automatically build your APK in the cloud** whenever you push code!

### Benefits:
- ✅ No need to install Android SDK locally
- ✅ Automated builds on every push
- ✅ Free cloud computing (up to 2,000 minutes/month)
- ✅ Easy APK sharing with others
- ✅ Professional CI/CD pipeline
- ✅ Build history saved on GitHub

---

## 📋 Prerequisites

1. **GitHub Account** (free at https://github.com)
2. **FocusApp project** (you have it!)
3. **Git installed** on your computer (download from https://git-scm.com)
4. **5 minutes of setup time**

---

## 🚀 Step-by-Step Setup

### Step 1: Create GitHub Repository

1. Go to https://github.com/new
2. Fill in:
   - **Repository name:** `focusapp` (or your preferred name)
   - **Description:** "Android app to stay focused by blocking distracting apps"
   - **Public** or **Private** (your choice)
   - **Add .gitignore:** Select "Android"
3. Click **"Create repository"**

---

### Step 2: Initialize Local Git Repository

Open terminal/command prompt in your FocusApp folder:

```bash
# Navigate to FocusApp folder
cd /path/to/FocusApp

# Initialize git
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit: FocusApp with all 6 features"

# Add remote GitHub repository
git remote add origin https://github.com/YOUR_USERNAME/focusapp.git

# Push to GitHub (main branch)
git branch -M main
git push -u origin main
```

**Replace `YOUR_USERNAME` with your actual GitHub username!**

---

### Step 3: Verify GitHub Actions Files

Check that GitHub Actions files are in your repository:

1. Go to your GitHub repository
2. Look for folder: `.github/workflows/`
3. You should see:
   - `build.yml` ← Builds on every push
   - `release.yml` ← Builds on version tags

If not there, they were created but need to be pushed. Run:
```bash
git add .github/
git commit -m "Add GitHub Actions workflows"
git push origin main
```

---

## ✨ Building Your APK

### Method 1: Automatic Build on Every Push ✅ EASIEST

**Every time you push code, GitHub automatically builds the APK!**

```bash
# Make a change to your code, then:
git add .
git commit -m "Update feature XYZ"
git push origin main

# GitHub Actions automatically builds! ✅
# Check status: Actions tab on GitHub
```

### Method 2: Manual Build (On Demand)

1. Go to your GitHub repository
2. Click **"Actions"** tab
3. Click **"Build APK"** in left sidebar
4. Click **"Run workflow"** button
5. Select branch (usually `main`)
6. Click **"Run workflow"**

GitHub will build immediately!

### Method 3: Release Build (Tagged Versions)

For official releases:

```bash
# Create a version tag
git tag -a v1.0.0 -m "FocusApp v1.0.0 - Initial release"

# Push the tag to GitHub
git push origin v1.0.0

# GitHub automatically:
# 1. Builds the APK
# 2. Creates a Release
# 3. Uploads APK for download
```

---

## 📥 Download Your APK

### From Automatic Builds:

1. Go to GitHub repository
2. Click **"Actions"** tab
3. Click latest **"Build APK"** workflow
4. Scroll down to **"Artifacts"** section
5. Download:
   - `focusapp-debug.apk` ← For testing
   - `focusapp-release-unsigned.apk` ← For release

### From Releases:

1. Go to GitHub repository
2. Click **"Releases"** on the right
3. Download APK directly from release page

---

## 📊 Understanding GitHub Actions

### What Happens When You Push:

```
You run: git push origin main
         │
         ▼
GitHub detects push to main branch
         │
         ▼
Triggers "Build APK" workflow
         │
         ├─ Sets up Java 11
         ├─ Downloads Gradle
         ├─ Runs: ./gradlew assembleDebug
         ├─ Runs: ./gradlew assembleRelease
         ├─ Uploads artifacts (APKs)
         │
         ▼
✅ Build complete in 5-10 minutes
Download APK from Actions → Artifacts
```

### Workflow Files Included:

**`.github/workflows/build.yml`**
- Triggers on: Push to main/master/develop
- Builds: Debug + Release APKs
- Artifacts: Available for download (30 days)

**`.github/workflows/release.yml`**
- Triggers on: New version tags (v1.0.0, v1.0.1, etc.)
- Creates: GitHub Release with APK
- Perfect for: Official releases

---

## 🔧 Customization

### Change Build Branches

Edit `.github/workflows/build.yml`:

```yaml
on:
  push:
    branches: [ main, master, develop ]  # Add/remove branches
```

### Change Java Version

Edit both `.yml` files:

```yaml
- name: Set up JDK 11
  uses: actions/setup-java@v3
  with:
    java-version: '11'  # Change to '17' for latest, '8' for older
```

### Add Email Notifications

Add to workflow after build step:

```yaml
- name: Send Email on Failure
  if: failure()
  uses: dawidd6/action-send-mail@v3
  with:
    server_address: smtp.gmail.com
    server_port: 465
    username: ${{ secrets.EMAIL_USERNAME }}
    password: ${{ secrets.EMAIL_PASSWORD }}
    subject: FocusApp Build Failed
    to: your-email@gmail.com
    from: github-actions@example.com
    body: Build failed! Check GitHub Actions for details.
```

---

## 🆘 Troubleshooting

### Build Failed?

1. Click **"Actions"** tab
2. Click failed workflow
3. Check **"Build Debug APK"** step for errors
4. Common fixes:
   - Missing `gradle.properties`
   - Wrong Java version
   - Syntax errors in code

### Can't Push to GitHub?

```bash
# Check remote
git remote -v

# Should show:
# origin  https://github.com/YOUR_USERNAME/focusapp.git

# If not, fix it:
git remote set-url origin https://github.com/YOUR_USERNAME/focusapp.git
```

### APK Not Downloading?

1. Verify build succeeded (green checkmark on Actions)
2. Artifacts expire after 90 days (adjust in settings if needed)
3. Try: Actions → [workflow name] → [specific run] → Artifacts

---

## 💡 Pro Tips

### 1. Use Git Branches for Features
```bash
# Create feature branch
git checkout -b feature/new-game

# Make changes, commit
git add .
git commit -m "Add new game feature"

# Push branch
git push origin feature/new-game

# GitHub builds automatically! ✅
# Then create Pull Request to merge
```

### 2. Monitor Build Status
```bash
# See build status in your terminal:
git log --oneline

# Check Actions page regularly for build results
```

### 3. Share APK with Others
```bash
# Anyone can download from GitHub Releases without account
# Just share the Release URL:
https://github.com/YOUR_USERNAME/focusapp/releases
```

### 4. Version Your Releases
```bash
git tag v1.0.0
git tag v1.1.0  # Add new game
git tag v1.2.0  # Bug fixes
```

---

## 📈 GitHub Actions Limits

**Free plan includes:**
- ✅ 2,000 build minutes/month
- ✅ Unlimited repositories
- ✅ Unlimited workflows
- ✅ 90-day artifact storage

**FocusApp usage:**
- Each build takes ~5 minutes
- You can do 400 builds/month!

---

## 🔐 Security Notes

### Never Commit Sensitive Files

These are in `.gitignore` (won't be uploaded):
- ✅ API keys
- ✅ Signing keystores
- ✅ Private keys
- ✅ Build files

### Signing Release APK (Advanced)

If you want to sign APK automatically:

1. Create keystore:
```bash
keytool -genkey -v -keystore focusapp.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias focusapp
```

2. Encode as Base64:
```bash
base64 focusapp.jks > keystore.txt
```

3. Add as GitHub Secret:
   - Go to: Settings → Secrets and variables → New secret
   - Name: `SIGNING_KEY`
   - Value: Paste base64 content

4. Update workflow to sign APK

---

## 🎓 Learning Path

1. **This week:** Push code and watch it build ✅
2. **Next week:** Create version tags (v1.0.0)
3. **Next month:** Set up automated signing
4. **Later:** Add tests, code analysis, deployments

---

## ✅ Quick Reference

| Task | Command |
|------|---------|
| Push code | `git push origin main` |
| Create tag | `git tag v1.0.0` |
| Push tag | `git push origin v1.0.0` |
| Check status | Visit GitHub Actions tab |
| Download APK | Actions → Artifacts → Download |

---

## 🚀 Next Steps

1. ✅ Create GitHub repository
2. ✅ Push FocusApp code
3. ✅ Watch GitHub Actions build APK
4. ✅ Download APK
5. ✅ Test on device
6. ✅ Make changes
7. ✅ Push again (auto-builds!)
8. ✅ Create release tags
9. ✅ Share with friends!

---

## 💪 You Did It!

You now have:
- ✅ Professional CI/CD pipeline
- ✅ Automatic APK builds
- ✅ Easy sharing via GitHub Releases
- ✅ Build history preserved
- ✅ No local build needed

**Welcome to professional app development! 🎉**

---

## 📞 Getting Help

- **GitHub Actions Docs:** https://docs.github.com/actions
- **Android Gradle Plugin:** https://developer.android.com/studio/releases/gradle-plugin
- **Git Tutorial:** https://git-scm.com/book/en/v2

Happy building! 🚀📱
